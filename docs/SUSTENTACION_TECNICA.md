# Sustentación Técnica - Prueba Técnica Sofka

## 1. Arquitectura del Proyecto

### Decisión: 2 Microservicios Separados

**¿Por qué?**
- Separación de responsabilidades (SoC)
- Escalabilidad independiente por servicio
- Fallos aislados entre servicios
- Despliegue independiente

**Microservicio Clientes (Puerto 8081)**
- Gestiona Personas y Clientes
- Entidad Cliente hereda de Persona (JPA Joined Table)
- Publica eventos de dominio cuando se crea/elimina un cliente

**Microservicio Cuentas (Puerto 8082)**
- Gestiona Cuentas y Movimientos
- Valida saldo disponible para retiros
- Escucha eventos de clientes para mantener consistencia

### Comunicación Asincrónica

**RabbitMQ como Message Broker**

```
Clientes Service ──publish──> ClienteCreadoEvent ──subscribe──> Cuentas Service
                 ──publish──> ClienteEliminadoEvent ──subscribe──> Cuentas Service
```

**Beneficio:** Los servicios no se comunican directamente, desacoplamiento temporal.

---

## 2. Patrones Implementados

### Repository Pattern
```java
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByClienteId(String clienteId);
    Optional<Cliente> findByIdentificacion(String identificacion);
}
```

### Service Layer Pattern
```java
@Service
public class ClienteService {
    public Cliente crearCliente(ClienteDTO dto) {
        // Lógica de negocio centralizada
    }
}
```

### Exception Handling Centralizado
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage()));
    }
}
```

---

## 3. Modelo de Datos

### Herencia JPA: Persona → Cliente

```
┌─────────────────────────────────────┐
│           persona (TABLE)            │
├─────────────────────────────────────┤
│ id (PK)                            │
│ nombre                              │
│ genero                              │
│ edad                                │
│ identificacion (UNIQUE)             │
│ direccion                           │
│ telefono                            │
│ fecha_creacion                      │
│ fecha_actualizacion                 │
└─────────────────────────────────────┘
              │
              │ JOINED (persona_id = id)
              ▼
┌─────────────────────────────────────┐
│           cliente (TABLE)            │
├─────────────────────────────────────┤
│ id (PK, FK → persona)              │
│ cliente_id (UNIQUE)                 │
│ contrasena                          │
│ estado                              │
└─────────────────────────────────────┘
```

### Relaciones

```
cliente ──1:N──> cuenta
cuenta ──1:N──> movimiento
```

---

## 4. Decisiones Técnicas Clave

### 4.1 ¿Por qué herencia JOINED y no SINGLE_TABLE?

| Tipo | Ventaja | Desventaja |
|------|---------|------------|
| JOINED | Normalización, queries específicas | JOINs en lecturas |
| SINGLE_TABLE | Sin JOINs, más rápido | Tabla extensa, posibles nulos |
| TABLE_PER_CLASS | Polimorfismo eficiente | DDL compleja |

**Elección:** JOINED → Mejor para normalización y claridad del modelo.

### 4.2 ¿Cómo se asegura la consistencia en movimientos?

```java
@Transactional
public MovimientoDTO registrarMovimiento(MovimientoDTO dto) {
    // Bloqueo pesimista para evitar race conditions
    Cuenta cuenta = cuentaRepository.findByIdWithLock(dto.getCuentaId())
            .orElseThrow(() -> new CuentaNoEncontradaException(...));
    
    // Validar saldo para retiros
    if (dto.getTipoMovimiento() == RETIRO 
        && dto.getValor().compareTo(cuenta.getSaldoDisponible()) > 0) {
        throw new SaldoNoDisponibleException(...);
    }
    
    // Actualización atómica en misma transacción
    BigDecimal nuevoSaldo = calcularNuevoSaldo(cuenta, dto);
    cuenta.setSaldoDisponible(nuevoSaldo);
    cuentaRepository.save(cuenta);
    
    Movimiento movimiento = mapper.toEntity(dto, nuevoSaldo);
    movimiento = movimientoRepository.save(movimiento);
    return mapper.toDTO(movimiento);
}
```

**¿Por qué @Transactional?**
- Garantiza atomicidad: saldo + movimiento se actualizan juntos
- Rollback automático si falla alguna validación
- Bloqueo pesimista (`SELECT FOR UPDATE`) evita race conditions

### 4.3 ¿Cómo se maneja la validación de saldo insuficiente?

```java
// Excepción personalizada
public class SaldoNoDisponibleException extends BusinessException {
    public SaldoNoDisponibleException(BigDecimal saldo, BigDecimal retiro) {
        super("SALDO_NO_DISPONIBLE", 
              String.format("Saldo %.2f insuficiente para retiro de %.2f", saldo, retiro),
              HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

// Respuesta JSON al cliente
{
    "codigo": "SALDO_NO_DISPONIBLE",
    "mensaje": "Saldo no disponible",
    "timestamp": "2025-04-06T10:30:00",
    "status": 422,
    "path": "/api/v1/movimientos"
}
```

### 4.4 Flyway vs JPA auto DDL

**Antes (ddl-auto: update):** ⚠️ NO PRODUCCIÓN

**Ahora (Flyway migrations):**
```
db/migration/
├── V1__Initial_schema.sql
└── V2__Seed_data.sql
```

**¿Por qué Flyway?**
1. Control de versiones del schema
2. Historial de cambios aplicados
3. Reproducibilidad en todos los ambientes
4. Rollback si algo falla
5. CI/CD automático

---

## 5. Endpoints Implementados

### F1: CRUD Completo

| Entidad | POST | GET | GET/{id} | PUT/{id} | DELETE/{id} |
|---------|------|-----|----------|----------|-------------|
| Clientes | /api/v1/clientes | /api/v1/clientes | /api/v1/clientes/{id} | /api/v1/clientes/{id} | /api/v1/clientes/{id} |
| Cuentas | /api/v1/cuentas | /api/v1/cuentas | /api/v1/cuentas/{id} | /api/v1/cuentas/{id} | /api/v1/cuentas/{id} |
| Movimientos | /api/v1/movimientos | /api/v1/movimientos | /api/v1/movimientos/{id} | - | /api/v1/movimientos/{id} |

### F2: Registro de Movimientos

```json
// Request
POST /api/v1/movimientos
{
    "tipoMovimiento": "DEPOSITO",
    "valor": 600.00,
    "cuentaId": 1
}

// Response (201 Created)
{
    "id": 5,
    "fecha": "2025-04-06",
    "tipoMovimiento": "DEPOSITO",
    "valor": 600.00,
    "saldo": 2600.00,
    "cuentaId": 1
}
```

### F3: Validación de Saldo

```json
// Request
POST /api/v1/movimientos
{
    "tipoMovimiento": "RETIRO",
    "valor": 5000.00,
    "cuentaId": 1
}

// Response (422 Unprocessable Entity)
{
    "codigo": "SALDO_NO_DISPONIBLE",
    "mensaje": "Saldo no disponible",
    "timestamp": "2025-04-06T10:30:00",
    "status": 422,
    "path": "/api/v1/movimientos"
}
```

### F4: Reporte de Estado de Cuenta

```
GET /api/v1/reportes?clienteId=1&fechaInicio=2025-01-01&fechaFin=2025-12-31
```

```json
{
    "cliente": {
        "id": 1,
        "nombre": "Jose Lema",
        "identificacion": "1234567890"
    },
    "fechaInicio": "2025-01-01",
    "fechaFin": "2025-12-31",
    "cuentas": [
        {
            "numeroCuenta": "478758",
            "tipoCuenta": "Ahorro",
            "saldoInicial": 2000.00,
            "saldoDisponible": 1425.00,
            "movimientos": [
                {
                    "fecha": "2025-04-05",
                    "tipoMovimiento": "RETIRO",
                    "valor": 575.00,
                    "saldo": 1425.00
                }
            ]
        }
    ]
}
```

---

## 6. Pruebas Implementadas

### F5: Pruebas Unitarias (33 tests)

**ClienteServiceTest (11 tests)**
- Crear cliente exitosamente
- Validar cliente duplicado (clienteId)
- Validar identificación duplicada
- Cliente no encontrado
- CRUD completo

**CuentaServiceTest (11 tests)**
- Crear cuenta exitosamente
- Validar cuenta duplicada (numeroCuenta)
- Cuenta no encontrada
- CRUD completo

**MovimientoServiceTest (11 tests)**
- Registrar depósito exitosamente
- Registrar retiro exitosamente
- Validar saldo insuficiente
- Cuenta no encontrada

### F6: Pruebas de Integración

Tests de servicios con mocks que validan:
- Flujo completo crear → depositar → retirar
- Actualización correcta de saldos
- Excepciones manejadas correctamente

---

## 7. Despliegue Docker (F7)

```yaml
# docker-compose.yml
services:
  clientes-service:
    build: ./clientes-service
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - postgres
      - rabbitmq

  cuentas-service:
    build: ./cuentas-service
    ports:
      - "8082:8082"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - postgres
      - rabbitmq

  postgres:
    image: postgres:15
    ports:
      - "5432:5432"

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
```

---

## 8. Preguntas Frecuentes de Entrevista

### P: ¿Por qué dos microservicios y no uno solo?
**R:** Separación de concerns, escalabilidad independiente, equipo puede trabajar en paralelo.

### P: ¿Cómo se comunican los microservicios?
**R:** Mediante eventos asincrónicos con RabbitMQ. No hay comunicación síncrona directa.

### P: ¿Qué pasa si RabbitMQ está caído?
**R:** Los eventos se pierden o se reencolan cuando RabbitMQ vuelve. Es un trade-off aceptable para esta prueba.

### P: ¿Cómo se evitan race conditions en movimientos?
**R:** `@Lock(PESSIMISTIC_WRITE)` en el repositorio genera `SELECT FOR UPDATE`, bloqueando la fila hasta commit.

### P: ¿Por qué herencia en lugar de una sola tabla?
**R:** Mejor modelado del dominio. Un Cliente ES una Persona. La herencia JOINED permite queries específicas por tipo.

### P: ¿Por qué JPA y no Entity Framework?
**R:** Spring Data JPA es el estándar en ecosistema Java/Spring.

---

## 9. Entregables Checklist

- Arquitectura 2 microservicios ✅
- Comunicación asincrónica (RabbitMQ) ✅
- CRUD Cliente, Cuenta, Movimiento (F1) ✅
- Registro movimientos + validación (F2, F3) ✅
- Reporte estado de cuenta (F4) ✅
- Pruebas unitarias (F5) ✅
- Pruebas integración (F6) ✅
- Despliegue Docker (F7) ✅
- Scripts SQL (BaseDatos.sql) ✅
- Colección Postman ✅
- Documentación README.md ✅

---

## 10. Implementaciones Futuras (No Incluidas)

```java
// TODO (FUTURO):
// - Autenticación JWT para proteger endpoints
// - Cache Redis para optimizar consultas frecuentes
// - OpenAPI/Swagger para documentación automática
// - Patrones de resiliencia (Circuit Breaker, Retry)
// - Métricas con Prometheus
// - Trazabilidad con distributed tracing
```

---

**Versión:** 1.0.0  
**Fecha:** Abril 2026
