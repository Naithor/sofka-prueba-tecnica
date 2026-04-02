-- =====================================================
-- SCRIPT SQL - BASE DE DATOS BANCARIA
-- Prueb@ Técnica Sofka - Microservicios Bancarios
-- =====================================================
-- Base de datos: PostgreSQL
-- Esquema: public
-- =====================================================

-- =====================================================
-- SECCIÓN 1: CREACIÓN DE ESQUEMA Y TABLAS (DDL)
-- =====================================================

-- Eliminar tablas si existen (en orden inverso de dependencias)
DROP TABLE IF EXISTS movimientos CASCADE;
DROP TABLE IF EXISTS cuentas CASCADE;
DROP TABLE IF EXISTS clientes CASCADE;
DROP TABLE IF EXISTS personas CASCADE;

-- Crear tabla personas (tabla base con herencia)
CREATE TABLE personas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero VARCHAR(20),
    edad INTEGER,
    identificacion VARCHAR(50) NOT NULL UNIQUE,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla clientes (hereda de personas)
CREATE TABLE clientes (
    id BIGINT PRIMARY KEY REFERENCES personas(id) ON DELETE CASCADE,
    cliente_id VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla cuentas
CREATE TABLE cuentas (
    id BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(50) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(50) NOT NULL,
    saldo_inicial DECIMAL(19, 2) NOT NULL,
    saldo_disponible DECIMAL(19, 2) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id BIGINT NOT NULL,
    cliente_identificacion VARCHAR(50),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla movimientos
CREATE TABLE movimientos (
    id BIGSERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    tipo_movimiento VARCHAR(50) NOT NULL,
    valor DECIMAL(19, 2) NOT NULL,
    saldo DECIMAL(19, 2) NOT NULL,
    cuenta_id BIGINT NOT NULL REFERENCES cuentas(id) ON DELETE CASCADE,
    cuenta_numero VARCHAR(50),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- SECCIÓN 2: ÍNDICES PARA OPTIMIZACIÓN
-- =====================================================

CREATE INDEX idx_cuentas_numero_cuenta ON cuentas(numero_cuenta);
CREATE INDEX idx_cuentas_cliente_id ON cuentas(cliente_id);
CREATE INDEX idx_movimientos_cuenta_id ON movimientos(cuenta_id);
CREATE INDEX idx_movimientos_fecha ON movimientos(fecha);
CREATE INDEX idx_personas_identificacion ON personas(identificacion);

-- =====================================================
-- SECCIÓN 3: DATOS DE PRUEBA (DML)
-- =====================================================

-- -----------------------------------------------------
-- 3.1 Insertar Clientes (Personas + Clientes)
-- -----------------------------------------------------

-- Cliente 1: Jose Lema
INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono, fecha_creacion, fecha_actualizacion)
VALUES ('Jose Lema', 'Masculino', 25, '1234567890', 'Otavalo sn y principal', '0982547851', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO clientes (id, cliente_id, contrasena, estado, fecha_creacion, fecha_actualizacion)
VALUES (1, 'JLEMA001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.Q.J.p7.rJvEH/0p0Xy', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cliente 2: Maria López
INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono, fecha_creacion, fecha_actualizacion)
VALUES ('Maria López', 'Femenino', 30, '9876543210', 'Quito centro', '0991234567', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO clientes (id, cliente_id, contrasena, estado, fecha_creacion, fecha_actualizacion)
VALUES (2, 'MLOPE002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.Q.J.p7.rJvEH/0p0Xy', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cliente 3: Carlos Pérez
INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono, fecha_creacion, fecha_actualizacion)
VALUES ('Carlos Pérez', 'Masculino', 35, '5555555555', 'Guayaquil norte', '0977654321', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO clientes (id, cliente_id, contrasena, estado, fecha_creacion, fecha_actualizacion)
VALUES (3, 'CPERE003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.Q.J.p7.rJvEH/0p0Xy', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cliente 4: Ana García (Inactivo)
INSERT INTO personas (nombre, genero, edad, identificacion, direccion, telefono, fecha_creacion, fecha_actualizacion)
VALUES ('Ana García', 'Femenino', 28, '1111111111', 'Cuenca sur', '0966666666', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO clientes (id, cliente_id, contrasena, estado, fecha_creacion, fecha_actualizacion)
VALUES (4, 'AGARC004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.Q.J.p7.rJvEH/0p0Xy', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -----------------------------------------------------
-- 3.2 Insertar Cuentas
-- -----------------------------------------------------

-- Cuenta 1: Ahorros de Jose Lema (Cliente 1)
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id, cliente_identificacion, fecha_creacion, fecha_actualizacion)
VALUES ('478758', 'AHORROS', 2000.00, 2000.00, TRUE, 1, '1234567890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cuenta 2: Corriente de Jose Lema (Cliente 1)
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id, cliente_identificacion, fecha_creacion, fecha_actualizacion)
VALUES ('478759', 'CORRIENTE', 5000.00, 5000.00, TRUE, 1, '1234567890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cuenta 3: Ahorros de Maria López (Cliente 2)
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id, cliente_identificacion, fecha_creacion, fecha_actualizacion)
VALUES ('123456', 'AHORROS', 10000.00, 10000.00, TRUE, 2, '9876543210', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cuenta 4: Ahorros de Carlos Pérez (Cliente 3)
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id, cliente_identificacion, fecha_creacion, fecha_actualizacion)
VALUES ('654321', 'AHORROS', 3500.00, 3500.00, TRUE, 3, '5555555555', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cuenta 5: Cuenta inactiva de Ana García (Cliente 4)
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id, cliente_identificacion, fecha_creacion, fecha_actualizacion)
VALUES ('111222', 'AHORROS', 500.00, 500.00, FALSE, 4, '1111111111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -----------------------------------------------------
-- 3.3 Insertar Movimientos (con fechas variadas)
-- -----------------------------------------------------

-- Movimientos de la Cuenta 1 (478758 - Jose Lema)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-01-15', 'DEPOSITO', 1000.00, 3000.00, 1, '478758', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-01-20', 'RETIRO', 500.00, 2500.00, 1, '478758', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-02-05', 'DEPOSITO', 200.00, 2700.00, 1, '478758', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-02-15', 'RETIRO', 700.00, 2000.00, 1, '478758', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-03-01', 'DEPOSITO', 1500.00, 3500.00, 1, '478758', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-03-10', 'RETIRO', 1200.00, 2300.00, 1, '478758', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-03-25', 'DEPOSITO', 800.00, 3100.00, 1, '478758', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Movimientos de la Cuenta 2 (478759 - Jose Lema - Corriente)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-01-10', 'DEPOSITO', 5000.00, 10000.00, 2, '478759', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-02-01', 'RETIRO', 2000.00, 8000.00, 2, '478759', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-03-15', 'RETIRO', 3000.00, 5000.00, 2, '478759', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Movimientos de la Cuenta 3 (123456 - Maria López)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-01-05', 'DEPOSITO', 5000.00, 15000.00, 3, '123456', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-02-10', 'DEPOSITO', 3000.00, 18000.00, 3, '123456', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-03-20', 'RETIRO', 4500.00, 13500.00, 3, '123456', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Movimientos de la Cuenta 4 (654321 - Carlos Pérez)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-02-15', 'DEPOSITO', 2000.00, 5500.00, 4, '654321', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id, cuenta_numero, fecha_creacion, fecha_actualizacion)
VALUES ('2026-03-05', 'RETIRO', 1500.00, 4000.00, 4, '654321', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- SECCIÓN 4: CONSULTAS DE VERIFICACIÓN
-- =====================================================

-- Verificar conteo de registros
SELECT 'Personas' AS tabla, COUNT(*) AS cantidad FROM personas
UNION ALL
SELECT 'Clientes', COUNT(*) FROM clientes
UNION ALL
SELECT 'Cuentas', COUNT(*) FROM cuentas
UNION ALL
SELECT 'Movimientos', COUNT(*) FROM movimientos;

-- Ver clientes con sus cuentas
SELECT 
    c.cliente_id,
    p.nombre,
    p.identificacion,
    c.estado AS cliente_activo,
    COUNT(cu.id) AS num_cuentas,
    SUM(cu.saldo_disponible) AS total_saldos
FROM clientes c
JOIN personas p ON c.id = p.id
LEFT JOIN cuentas cu ON c.id = cu.cliente_id
GROUP BY c.id, c.cliente_id, p.nombre, p.identificacion, c.estado
ORDER BY c.cliente_id;

-- Estado de cuenta del Cliente 1 (Jose Lema) - Reporte F4
SELECT 
    cu.numero_cuenta,
    cu.tipo_cuenta,
    cu.saldo_inicial,
    cu.saldo_disponible,
    cu.estado AS cuenta_activa,
    m.fecha,
    m.tipo_movimiento,
    m.valor,
    m.saldo AS saldo_despues_movimiento
FROM cuentas cu
LEFT JOIN movimientos m ON cu.id = m.cuenta_id
WHERE cu.cliente_id = 1
ORDER BY cu.numero_cuenta, m.fecha;

-- =====================================================
-- SECCIÓN 5: DATOS PARA PRUEBAS ADICIONALES
-- =====================================================

-- Nota: La contraseña encriptada es '1234' (BCrypt)
-- Para pruebas de login usar:
--   username: JLEMA001, password: 1234
--   username: MLOPE002, password: 1234
--   username: CPERE003, password: 1234

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
