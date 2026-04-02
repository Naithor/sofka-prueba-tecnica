package com.naithor.sofkapruebatecnica.cuentas.service;

import com.naithor.sofkapruebatecnica.cuentas.dto.CuentaReporteDTO;
import com.naithor.sofkapruebatecnica.cuentas.dto.EstadoCuentaDTO;
import com.naithor.sofkapruebatecnica.cuentas.dto.MovimientoReporteDTO;
import com.naithor.sofkapruebatecnica.cuentas.entity.Cuenta;
import com.naithor.sofkapruebatecnica.cuentas.entity.Movimiento;
import com.naithor.sofkapruebatecnica.cuentas.repository.CuentaRepository;
import com.naithor.sofkapruebatecnica.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para generar reportes - F4
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {
    
    private static final Logger log = LoggerFactory.getLogger(ReporteService.class);
    
    private final CuentaRepository cuentaRepository;
    private final MovimientoService movimientoService;
    
    /**
     * Generar reporte de estado de cuenta por cliente y rango de fechas - F4
     */
    public EstadoCuentaDTO generarEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Generando reporte de estado de cuenta para cliente {} entre {} y {}", 
            clienteId, fechaInicio, fechaFin);
        
        // Obtener todas las cuentas del cliente
        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);
        
        if (cuentas.isEmpty()) {
            throw new BusinessException("CLIENTE_SIN_CUENTAS", 
                "El cliente no tiene cuentas registradas");
        }
        
        // Crear DTO de respuesta
        EstadoCuentaDTO estadoCuenta = new EstadoCuentaDTO();
        estadoCuenta.setClienteId(clienteId);
        estadoCuenta.setClienteIdentificacion(cuentas.get(0).getClienteIdentificacion());
        
        // Procesar cada cuenta
        List<CuentaReporteDTO> cuentasReporte = cuentas.stream()
            .map(cuenta -> generarReporteCuenta(cuenta, fechaInicio, fechaFin))
            .collect(Collectors.toList());
        
        estadoCuenta.setCuentas(cuentasReporte);
        
        log.info("Reporte generado con éxito para cliente {}", clienteId);
        
        return estadoCuenta;
    }
    
    /**
     * Generar reporte de una cuenta específica
     */
    private CuentaReporteDTO generarReporteCuenta(Cuenta cuenta, LocalDate fechaInicio, LocalDate fechaFin) {
        CuentaReporteDTO cuentaReporte = new CuentaReporteDTO();
        cuentaReporte.setNumeroCuenta(cuenta.getNumeroCuenta());
        cuentaReporte.setTipo(cuenta.getTipoCuenta());
        cuentaReporte.setSaldoInicial(cuenta.getSaldoInicial());
        cuentaReporte.setSaldoActual(cuenta.getSaldoDisponible());
        cuentaReporte.setEstado(cuenta.getEstado());
        
        // Obtener movimientos de la cuenta en el rango de fechas
        List<Movimiento> movimientos = movimientoService.obtenerMovimientosCliente(
            cuenta.getClienteId(), fechaInicio, fechaFin
        ).stream()
            .filter(m -> m.getCuenta().getId().equals(cuenta.getId()))
            .collect(Collectors.toList());
        
        // Convertir a DTO de reporte
        List<MovimientoReporteDTO> movimientosReporte = movimientos.stream()
            .map(this::mapMovimientoAReporte)
            .collect(Collectors.toList());
        
        cuentaReporte.setMovimientos(movimientosReporte);
        
        return cuentaReporte;
    }
    
    /**
     * Mapear movimiento a DTO de reporte
     */
    private MovimientoReporteDTO mapMovimientoAReporte(Movimiento movimiento) {
        MovimientoReporteDTO dto = new MovimientoReporteDTO();
        dto.setFecha(movimiento.getFecha());
        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setMovimiento(movimiento.getValor());
        dto.setSaldoDisponible(movimiento.getSaldo());
        return dto;
    }
}

