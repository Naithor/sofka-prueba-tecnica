package com.naithor.sofkapruebatecnica.shared.enums;

public enum TipoMovimiento {
    DEPOSITO,
    RETIRO;
    
    public static TipoMovimiento fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Tipo de movimiento no puede ser nulo");
        }
        try {
            return TipoMovimiento.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de movimiento inválido: " + value + ". Valores válidos: DEPOSITO, RETIRO");
        }
    }
}
