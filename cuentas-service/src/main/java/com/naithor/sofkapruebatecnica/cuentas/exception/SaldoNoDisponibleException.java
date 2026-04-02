package com.naithor.sofkapruebatecnica.cuentas.exception;

import com.naithor.sofkapruebatecnica.shared.exception.BusinessException;

/**
 * Excepción cuando no hay saldo disponible para un movimiento
 */
public class SaldoNoDisponibleException extends BusinessException {
    
    public SaldoNoDisponibleException(String mensaje) {
        super("SALDO_NO_DISPONIBLE", mensaje);
    }
    
    public SaldoNoDisponibleException(String mensaje, Throwable cause) {
        super("SALDO_NO_DISPONIBLE", mensaje, cause);
    }
}

