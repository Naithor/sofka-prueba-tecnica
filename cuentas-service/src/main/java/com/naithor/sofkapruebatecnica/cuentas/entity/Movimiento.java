package com.naithor.sofkapruebatecnica.cuentas.entity;

import com.naithor.sofkapruebatecnica.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "movimientos", indexes = {
    @Index(name = "idx_cuenta_id", columnList = "cuenta_id"),
    @Index(name = "idx_fecha", columnList = "fecha")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    
    @Column(name = "tipo_movimiento", nullable = false, length = 50)
    private String tipoMovimiento;
    
    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "saldo", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private Cuenta cuenta;
    
    @Column(name = "cuenta_numero")
    private String cuentaNumero;
}
