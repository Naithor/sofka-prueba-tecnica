package com.naithor.sofkapruebatecnica.shared.enums;

public enum TipoCuenta {
    AHORROS("Ahorros"),
    CORRIENTE("Corriente");
    
    private final String descripcion;
    
    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public static TipoCuenta fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Tipo de cuenta no puede ser nulo");
        }
        String normalized = value.toUpperCase().trim();
        if (normalized.equals("AHORRO") || normalized.equals("AHORROS")) {
            return AHORROS;
        }
        try {
            return TipoCuenta.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de cuenta inválido: " + value + ". Valores válidos: AHORROS, CORRIENTE");
        }
    }
}
