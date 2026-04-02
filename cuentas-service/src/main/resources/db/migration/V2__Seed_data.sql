-- V2__Seed_data.sql
-- Datos iniciales para pruebas

-- Cuenta de Jose Lema
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id, cliente_identificacion)
VALUES ('478758', 'AHORROS', 2000.00, 2000.00, TRUE, 1, '1234567890');

-- Cuenta Corriente de Jose Lema
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id, cliente_identificacion)
VALUES ('478759', 'CORRIENTE', 5000.00, 5000.00, TRUE, 1, '1234567890');

-- Cuenta de Maria Lopez
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id, cliente_identificacion)
VALUES ('123456', 'AHORROS', 10000.00, 10000.00, TRUE, 2, '9876543210');
