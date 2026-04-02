package com.naithor.sofkapruebatecnica.cuentas.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoDTO {
    
    private Long id;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
    
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "^(DEPOSITO|RETIRO)$", message = "El tipo de movimiento debe ser DEPOSITO o RETIRO")
    private String tipoMovimiento;
    
    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor a 0")
    private BigDecimal valor;
    
    private BigDecimal saldo;
    
    @NotNull(message = "El ID de cuenta es obligatorio")
    private Long cuentaId;
    
    private String cuentaNumero;
}
