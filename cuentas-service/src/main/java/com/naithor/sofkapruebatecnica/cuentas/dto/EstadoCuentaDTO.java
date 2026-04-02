package com.naithor.sofkapruebatecnica.cuentas.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoCuentaDTO {
    private Long clienteId;
    private String clienteNombre;
    private String clienteIdentificacion;
    private List<CuentaReporteDTO> cuentas;
}
