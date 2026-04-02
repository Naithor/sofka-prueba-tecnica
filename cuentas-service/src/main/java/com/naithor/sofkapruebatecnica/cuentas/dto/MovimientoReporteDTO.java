package com.naithor.sofkapruebatecnica.cuentas.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoReporteDTO {
    private LocalDate fecha;
    private String tipoMovimiento;
    private BigDecimal movimiento;
    private BigDecimal saldoDisponible;
}
