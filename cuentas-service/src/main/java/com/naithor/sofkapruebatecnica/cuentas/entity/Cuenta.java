package com.naithor.sofkapruebatecnica.cuentas.entity;

import com.naithor.sofkapruebatecnica.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cuentas", indexes = {
    @Index(name = "idx_numero_cuenta", columnList = "numero_cuenta", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_cuenta", nullable = false, unique = true, length = 50)
    private String numeroCuenta;
    
    @Column(name = "tipo_cuenta", nullable = false, length = 50)
    private String tipoCuenta;
    
    @Column(name = "saldo_inicial", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoInicial;
    
    @Column(name = "saldo_disponible", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldoDisponible;
    
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
    
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;
    
    @Column(name = "cliente_identificacion")
    private String clienteIdentificacion;
}
