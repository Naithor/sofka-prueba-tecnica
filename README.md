# Sofka Prueba Técnica - Arquitectura Microservicios Bancarios

## 📋 Descripción

Solución completa de microservicios para gestión de clientes, cuentas y movimientos bancarios. Implementado con Spring Boot 4.0.4, PostgreSQL, RabbitMQ y Docker.

**Nivel:** Senior - Implementa todas las funcionalidades F1-F7 con arquitectura escalable, resiliente y desacoplada.

---

## 🏗️ Arquitectura

### Componentes Principales

1. **Clientes Microservice** (Puerto 8081)
   - Gestión de Personas y Clientes
   - Herencia JPA: Persona → Cliente
   - Publica eventos de dominio al crear/eliminar clientes

2. **Cuentas Microservice** (Puerto 8082)
   - Gestión de Cuentas y Movimientos
   - Valida saldo disponible antes de retiros
   - Escucha eventos de clientes para cascade
   - Genera reportes de estado de cuenta

3. **Message Broker** (RabbitMQ)
   - Comunicación asincrónica entre microservicios
   - Eventos: `ClienteCreadoEvent`, `ClienteEliminadoEvent`

4. **Bases de Datos** (PostgreSQL)
   - `db_clientes`: Datos de personas y clientes
   - `db_cuentas`: Cuentas y movimientos (con índices optimizados)

### Patrones Implementados

- ✅ **Domain-Driven Design (DDD)** - Entidades, servicios y eventos de dominio
- ✅ **Repository Pattern** - Spring Data JPA
- ✅ **Service Layer Pattern** - Lógica de negocio centralizada
- ✅ **Event-Driven Architecture** - RabbitMQ para eventos asincrónico
- ✅ **CQRS** - Consultas separadas para reportes
- ✅ **Exception Handling** - GlobalExceptionHandler con códigos de error

---

## 📦 Funcionalidades Implementadas

### F1: CRUD Completo
- **Endpoints Clientes:**
  - `POST /api/v1/clientes` - Crear cliente
  - `GET /api/v1/clientes` - Listar todos
  - `GET /api/v1/clientes/{id}` - Obtener por ID
  - `PUT /api/v1/clientes/{id}` - Actualizar
  - `DELETE /api/v1/clientes/{id}` - Eliminar

- **Endpoints Cuentas:**
  - `POST /api/v1/cuentas` - Crear cuenta
  - `GET /api/v1/cuentas` - Listar todas
  - `GET /api/v1/cuentas/{id}` - Obtener por ID
  - `PUT /api/v1/cuentas/{id}` - Actualizar
  - `DELETE /api/v1/cuentas/{id}` - Eliminar

- **Endpoints Movimientos:**
  - `POST /api/v1/movimientos` - Registrar movimiento
  - `GET /api/v1/movimientos` - Listar todos
  - `GET /api/v1/movimientos/{id}` - Obtener por ID
  - `DELETE /api/v1/movimientos/{id}` - Eliminar

### F2: Registro de Movimientos
- Depósitos (suma al saldo)
- Retiros (resta al saldo)
- Actualización atómica saldo + movimiento
- Registro de transacciones con auditoría

### F3: Validación de Saldo
- Excepción `SaldoNoDisponibleException` con código `SALDO_NO_DISPONIBLE`
- HTTP 422 (Unprocessable Entity)
- Respuesta JSON estructurada con detalles del error

### F4: Reportes
- `GET /api/v1/reportes?clienteId={id}&fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD`
- Retorna estado de cuenta con:
  - Datos del cliente
  - Cuentas asociadas con saldos
  - Detalle de movimientos por rango de fechas
  - Formato JSON estructurado

### F5: Pruebas Unitarias
- **ClienteServiceTest** - Cobertura de servicio con Mockito
  - Crear cliente exitosamente
  - Validar duplicados
  - CRUD completo

### F6: Pruebas de Integración
- **MovimientoControllerIT** - TestContainers + PostgreSQL
  - Flujo completo: crear cuenta → depósito → retiro
  - Validación de saldo insuficiente
  - Actualización correcta de saldos

### F7: Despliegue en Docker
- Docker Compose con 5 servicios
- Health checks en cada contenedor
- Scripts SQL para inicialización
- Perfiles de configuración (local/docker)

---

## 🚀 Guía de Instalación

### Requisitos Previos
- Java 26 (JDK)
- Maven 3.9+
- Docker & Docker Compose
- Git

### Opción 1: Despliegue Local (Desarrollo)

#### 1. Clonar Repositorio
```bash
git clone <repo-url>
cd sofka-prueba-tecnica
```

#### 2. Instalar Dependencias
```bash
mvn clean install -DskipTests
```

#### 3. Configurar Base de Datos Local

**PostgreSQL - Cliente Service:**
```sql
CREATE DATABASE db_clientes;
-- Ejecutar: scripts/init-clientes.sql
```

**PostgreSQL - Cuentas Service:**
```sql
CREATE DATABASE db_cuentas;
-- Ejecutar: scripts/init-cuentas.sql
```

**RabbitMQ:**
```bash
# Docker local
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management-alpine
```

#### 4. Ejecutar Microservicios

**Terminal 1 - Clientes:**
```bash
cd clientes-service
mvn spring-boot:run
# http://localhost:8081/api/v1/clientes
```

**Terminal 2 - Cuentas:**
```bash
cd cuentas-service
mvn spring-boot:run
# http://localhost:8082/api/v1/cuentas
```

### Opción 2: Despliegue con Docker Compose (Recomendado)

#### 1. Construir y Ejecutar
```bash
docker-compose up -d --build
```

#### 2. Verificar Estado
```bash
docker-compose ps
# Todos los servicios deben estar "healthy" después de 60 segundos
```

#### 3. URLs de Acceso
- **Clientes Service:** http://localhost:8081/api/v1/clientes
- **Cuentas Service:** http://localhost:8082/api/v1/cuentas
- **RabbitMQ Management:** http://localhost:15672 (guest/guest)
- **pgAdmin:** http://localhost:5050 (admin@admin.com/admin)

#### 4. Detener
```bash
docker-compose down -v
```

---

## 📝 Ejemplos de Uso

### 1. Crear Cliente

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

### 2. Crear Cuenta

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

### 3. Registrar Depósito

```bash
curl -X POST http://localhost:8082/api/v1/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "fecha": "2025-02-10",
    "tipoMovimiento": "DEPOSITO",
    "valor": 600.00,
    "cuentaId": 1
  }'
```

### 4. Registrar Retiro (Saldo Insuficiente)

```bash
curl -X POST http://localhost:8082/api/v1/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "fecha": "2025-02-11",
    "tipoMovimiento": "RETIRO",
    "valor": 5000.00,
    "cuentaId": 1
  }'

# Respuesta: HTTP 422
# {"codigo": "SALDO_NO_DISPONIBLE", "mensaje": "Saldo no disponible...", ...}
```

### 5. Generar Reporte

```bash
curl -X GET "http://localhost:8082/api/v1/reportes?clienteId=1&fechaInicio=2025-02-01&fechaFin=2025-02-28"
```

---

## 🧪 Ejecutar Pruebas

### Pruebas Unitarias
```bash
mvn test -Dtest=ClienteServiceTest
```

### Pruebas de Integración
```bash
mvn test -Dtest=MovimientoControllerIT
```

### Cobertura Completa
```bash
mvn clean test
```

---

## 📊 Colección Postman

Archivo incluido: **`Sofka_Prueba_Tecnica.postman_collection.json`**

### Importar en Postman:
1. Abrir Postman
2. Click en "Import"
3. Seleccionar archivo `Sofka_Prueba_Tecnica.postman_collection.json`
4. Colección estará disponible con todos los endpoints

**Carpetas incluidas:**
- CLIENTES (5 requests)
- CUENTAS (6 requests)
- MOVIMIENTOS (8 requests)
- REPORTES (2 requests)

---

## 🗄️ Base de Datos

### Esquema - Clientes

```sql
personas (herencia)
├── id (PK)
├── nombre
├── genero
├── edad
├── identificacion (UNIQUE)
├── direccion
├── telefono
└── fecha_creacion, fecha_actualizacion

clientes (heredada de personas)
├── id (FK → personas)
├── cliente_id (UNIQUE)
├── contrasena
└── estado
```

### Esquema - Cuentas

```sql
cuentas
├── id (PK)
├── numero_cuenta (UNIQUE)
├── tipo_cuenta
├── saldo_inicial
├── saldo_disponible
├── estado
├── cliente_id (FK)
└── fecha_creacion, fecha_actualizacion

movimientos
├── id (PK)
├── fecha
├── tipo_movimiento (DEPOSITO|RETIRO)
├── valor
├── saldo
├── cuenta_id (FK → cuentas)
└── fecha_creacion, fecha_actualizacion
```

---

## 🔍 Manejo de Errores

### Códigos de Error Implementados

| Código | HTTP | Descripción |
|--------|------|-------------|
| `CLIENTE_DUPLICADO` | 400 | ClienteId ya existe |
| `IDENTIFICACION_DUPLICADA` | 400 | Identificación ya existe |
| `CLIENTE_NO_ENCONTRADO` | 400 | Cliente no existe |
| `SALDO_NO_DISPONIBLE` | 422 | Saldo insuficiente para retiro |
| `CUENTA_DUPLICADA` | 400 | Número de cuenta existe |
| `CUENTA_NO_ENCONTRADA` | 400 | Cuenta no existe |
| `MOVIMIENTO_NO_ENCONTRADO` | 400 | Movimiento no existe |
| `VALIDATION_ERROR` | 400 | Error de validación DTO |
| `ERROR_INTERNO` | 500 | Error interno del servidor |

### Respuesta de Error Estándar

```json
{
  "codigo": "SALDO_NO_DISPONIBLE",
  "mensaje": "Saldo no disponible para realizar el retiro de 5000.00",
  "timestamp": "2025-02-25T11:30:00",
  "status": 422,
  "path": "/api/v1/movimientos"
}
```

---

## 📚 Tecnologías Utilizadas

| Componente | Versión | Propósito |
|-----------|---------|----------|
| Java | 26 | Lenguaje base |
| Spring Boot | 4.0.4 | Framework |
| Spring Data JPA | 4.0.4 | ORM |
| Spring AMQP | 4.0.4 | Message Broker |
| PostgreSQL | 15 | Base de datos |
| RabbitMQ | 3.12 | Event streaming |
| Lombok | Latest | Boilerplate reduction |
| Mockito | Latest | Unit testing |
| TestContainers | 1.19.3 | Integration testing |
| Docker | Latest | Containerization |

---

## 📖 Decisiones Técnicas Senior

### 1. Arquitectura Desacoplada
- **Decisión:** Dos microservicios separados con comunicación asincrónica
- **Beneficio:** Escalabilidad independiente, fallos aislados
- **Trade-off:** Mayor complejidad operacional

### 2. Eventos de Dominio (RabbitMQ)
- **Decisión:** Publicar eventos al crear/eliminar clientes
- **Beneficio:** Consistencia eventual, desacoplamiento temporal
- **Implementación:** Listener en cuentas para desactivar cuentas cuando cliente se elimina

### 3. Herencia JPA (Joined Table)
- **Decisión:** Persona → Cliente con herencia JOINED
- **Beneficio:** Relación clara, queries optimizadas
- **Alternativa desestimada:** Single table (menos performante con muchas entidades)

### 4. Índices Estratégicos
- **Campos indexados:** `identificacion`, `cliente_id`, `numero_cuenta`, `fecha`, `cuenta_id`
- **Beneficio:** Queries de reportes eficientes
- **Costo:** Escritura ligeramente más lenta (acceptable)

### 5. Transacciones Atómicas
- **Decisión:** `@Transactional` en ServiceLayer
- **Beneficio:** Consistencia ACID: movimiento + actualización saldo en una transacción
- **Rollback automático** si falla validación de saldo

### 6. GlobalExceptionHandler
- **Decisión:** Centralizar manejo de errores en controladores
- **Beneficio:** Respuestas JSON consistentes
- **Codes:** Códigos de error personalizados para debugging

### 7. TestContainers para Integración
- **Decisión:** Usar PostgreSQL real en tests (no mocks)
- **Beneficio:** Pruebas más realistas, detecta issues de schema/queries
- **Trade-off:** Tests más lentos pero más confiables

---

## 🔐 Consideraciones de Seguridad

⚠️ **Nota:** Esta es una prueba técnica. Para producción implementar:
- Autenticación JWT/OAuth2
- Encriptación de contraseñas (BCrypt)
- Rate limiting
- SQL Injection prevention (JPA nativa)
- HTTPS/TLS
- Audit logging

---

## 📋 Checklist de Entregables

- ✅ Arquitectura de 2 microservicios
- ✅ Comunicación asincrónica (RabbitMQ)
- ✅ CRUD para Clientes, Cuentas, Movimientos (F1)
- ✅ Registro de movimientos con validación (F2, F3)
- ✅ Reporte de estado de cuenta (F4)
- ✅ Prueba unitaria ClienteService (F5)
- ✅ Prueba de integración Movimientos (F6)
- ✅ Despliegue Docker Compose (F7)
- ✅ Scripts SQL de inicialización
- ✅ Colección Postman JSON
- ✅ Documentación README (este archivo)

---

## 🤝 Soporte

Para preguntas sobre la arquitectura o implementación, referirse a:
- Documentación inline en código (comentarios)
- Estructura de paquetes indica la arquitectura
- Tests como ejemplos de uso

---

**Versión:** 1.0.0  
**Fecha:** Marzo 2025  
**Autor:** Desarrollador Senior - Sofka Test

