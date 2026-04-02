package com.naithor.sofkapruebatecnica.cuentas.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaDTO {
    
    private Long id;
    
    @NotBlank(message = "El número de cuenta es obligatorio")
    @Size(min = 5, max = 50, message = "El número de cuenta debe tener entre 5 y 50 caracteres")
    private String numeroCuenta;
    
    @NotBlank(message = "El tipo de cuenta es obligatorio")
    private String tipoCuenta;
    
    @NotNull(message = "El saldo inicial es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo inicial debe ser mayor o igual a 0")
    private BigDecimal saldoInicial;
    
    private BigDecimal saldoDisponible;
    
    private Boolean estado = true;
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
    
    private String clienteIdentificacion;
}
