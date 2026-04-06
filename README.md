# Sofka Prueba Técnica - Arquitectura Microservicios Bancarios

## Descripción

Solución de microservicios para gestión de clientes, cuentas y movimientos bancarios.
Implementado con Spring Boot 4.0.4, PostgreSQL, RabbitMQ y Docker.

**Nivel:** Senior - Implementa funcionalidades F1-F7.

---

## Funcionalidades Implementadas (F1-F7)

### F1: CRUD Completo (Cliente, Cuenta, Movimiento)
- **Endpoints Clientes:** `/api/v1/clientes`
  - `POST /api/v1/clientes` - Crear
  - `GET /api/v1/clientes` - Listar
  - `GET /api/v1/clientes/{id}` - Obtener
  - `PUT /api/v1/clientes/{id}` - Actualizar
  - `DELETE /api/v1/clientes/{id}` - Eliminar

- **Endpoints Cuentas:** `/api/v1/cuentas`
  - `POST /api/v1/cuentas` - Crear
  - `GET /api/v1/cuentas` - Listar
  - `GET /api/v1/cuentas/{id}` - Obtener
  - `PUT /api/v1/cuentas/{id}` - Actualizar
  - `DELETE /api/v1/cuentas/{id}` - Eliminar

- **Endpoints Movimientos:** `/api/v1/movimientos`
  - `POST /api/v1/movimientos` - Registrar
  - `GET /api/v1/movimientos` - Listar
  - `GET /api/v1/movimientos/{id}` - Obtener
  - `DELETE /api/v1/movimientos/{id}` - Eliminar

### F2: Registro de Movimientos
- Valores positivos (DEPOSITO) y negativos (RETIRO)
- Actualización atómica del saldo disponible
- Registro completo de transacciones

### F3: Validación de Saldo
- Código error: `SALDO_NO_DISPONIBLE`
- HTTP 422 (Unprocessable Entity)
- Mensaje: "Saldo no disponible"

### F4: Reportes de Estado de Cuenta
- Endpoint: `GET /api/v1/reportes?clienteId={id}&fechaInicio={date}&fechaFin={date}`
- Incluye: Cliente, cuentas con saldos, movimientos por rango de fechas
- Formato JSON

### F5: Pruebas Unitarias
- ClienteServiceTest (11 tests)
- CuentaServiceTest (11 tests)
- MovimientoServiceTest (11 tests)

### F6: Pruebas de Integración
- Tests de servicios con mocks

### F7: Despliegue Docker
- docker-compose.yml con todos los servicios

---

## Implementaciones Futuras (No incluidas en entregable)

```java
// TODO (FUTURO): Agregar autenticación JWT
// import org.springframework.security:spring-security-jwt
// Implementar: JwtAuthenticationFilter, AuthController

// TODO (FUTURO): Agregar cache Redis
// import org.springframework.boot:spring-boot-starter-data-redis
// Implementar: RedisConfig, @Cacheable en servicios

// TODO (FUTURO): Agregar documentación OpenAPI
// import org.springdoc:springdoc-openapi-starter-webmvc-ui
// Implementar: OpenApiConfig, @Operation annotations

// TODO (FUTURO): Agregar resiliencia
// import io.github.resilience4j:resilience4j-spring-boot3
// Implementar: CircuitBreaker, RateLimiter

// TODO (FUTURO): Agregar paginación avanzada
// Implementar: PageResponse en todos los listados
```

---

## Arquitectura

### Microservicios

1. **Clientes Service** (Puerto 8081)
   - Gestión de Personas y Clientes
   - Herencia JPA: Persona → Cliente
   - Eventos: ClienteCreadoEvent, ClienteEliminadoEvent

2. **Cuentas Service** (Puerto 8082)
   - Gestión de Cuentas y Movimientos
   - Valida saldo para retiros
   - Genera reportes de estado de cuenta

3. **RabbitMQ** (Puerto 5672)
   - Comunicación asincrónica entre microservicios

4. **PostgreSQL** (Puerto 5432)
   - db_clientes: Personas y clientes
   - db_cuentas: Cuentas y movimientos

### Patrones Implementados

- Repository Pattern (Spring Data JPA)
- Service Layer Pattern
- Event-Driven Architecture (RabbitMQ)
- Exception Handling centralizado

---

## Guía de Instalación

### Requisitos
- Java 21
- Maven 3.9+
- Docker & Docker Compose

### Despliegue

```bash
# Construir y ejecutar
docker-compose up -d --build

# Verificar estado
docker-compose ps

# Endpoints:
# Clientes: http://localhost:8081/api/v1/clientes
# Cuentas: http://localhost:8082/api/v1/cuentas
# RabbitMQ: http://localhost:15672 (guest/guest)
```

### Desarrollo Local

```bash
# Sin Docker
docker-compose up -d postgres rabbitmq

# Ejecutar servicios
mvn spring-boot:run -pl clientes-service
mvn spring-boot:run -pl cuentas-service
```

---

## Ejemplos de Uso

### Crear Cliente
```bash
curl -X POST http://localhost:8081/api/v1/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Jose Lema",
    "genero": "Masculino",
    "edad": 35,
    "identificacion": "1234567890",
    "direccion": "Otavalo sn y principal",
    "telefono": "0982547851",
    "clienteId": "JLEMA001",
    "contrasena": "1234",
    "estado": true
  }'
```

### Crear Cuenta
```bash
curl -X POST http://localhost:8082/api/v1/cuentas \
  -H "Content-Type: application/json" \
  -d '{
    "numeroCuenta": "478758",
    "tipoCuenta": "Ahorro",
    "saldoInicial": 2000.00,
    "estado": true,
    "clienteId": 1,
    "clienteIdentificacion": "1234567890"
  }'
```

### Registrar Movimiento
```bash
# Depósito
curl -X POST http://localhost:8082/api/v1/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "tipoMovimiento": "DEPOSITO",
    "valor": 600.00,
    "cuentaId": 1
  }'

# Retiro
curl -X POST http://localhost:8082/api/v1/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "tipoMovimiento": "RETIRO",
    "valor": 575.00,
    "cuentaId": 1
  }'
```

### Generar Reporte
```bash
curl "http://localhost:8082/api/v1/reportes?clienteId=1&fechaInicio=2025-01-01&fechaFin=2025-12-31"
```

---

## Manejo de Errores

| Código | HTTP | Descripción |
|--------|------|-------------|
| CLIENTE_DUPLICADO | 400 | ClienteId ya existe |
| IDENTIFICACION_DUPLICADA | 400 | Identificación ya existe |
| CLIENTE_NO_ENCONTRADO | 404 | Cliente no existe |
| SALDO_NO_DISPONIBLE | 422 | Saldo insuficiente |
| CUENTA_DUPLICADA | 400 | Número de cuenta existe |
| CUENTA_NO_ENCONTRADA | 404 | Cuenta no existe |
| MOVIMIENTO_NO_ENCONTRADO | 404 | Movimiento no existe |

---

## Base de Datos

### Esquema - Clientes
```
personas (tabla padre)
├── id (PK)
├── nombre
├── genero
├── edad
├── identificacion (UNIQUE)
├── direccion
├── telefono
└── fechas auditoría

clientes (heredada de personas)
├── cliente_id (UNIQUE)
└── contrasena
```

### Esquema - Cuentas
```
cuentas
├── numero_cuenta (UNIQUE)
├── tipo_cuenta
├── saldo_inicial
├── saldo_disponible
├── estado
├── cliente_id
└── fechas auditoría

movimientos
├── fecha
├── tipo_movimiento
├── valor
├── saldo
├── cuenta_id (FK)
└── fechas auditoría
```

---

## Pruebas

```bash
# Todas las pruebas
mvn test

# Solo unit tests
mvn test -Dtest="*ServiceTest"

# Cobertura
mvn test jacoco:report
```

---

## Entregables Checklist

- Arquitectura 2 microservicios
- Comunicación asincrónica (RabbitMQ)
- CRUD Cliente, Cuenta, Movimiento (F1)
- Registro movimientos + validación (F2, F3)
- Reporte estado de cuenta (F4)
- Pruebas unitarias (F5)
- Pruebas integración (F6)
- Despliegue Docker (F7)
- Scripts SQL
- Colección Postman

---

**Versión:** 1.0.0  
**Fecha:** Abril 2026
