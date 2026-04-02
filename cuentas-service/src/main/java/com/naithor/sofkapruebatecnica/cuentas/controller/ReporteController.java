package com.naithor.sofkapruebatecnica.cuentas.controller;

import com.naithor.sofkapruebatecnica.cuentas.dto.EstadoCuentaDTO;
import com.naithor.sofkapruebatecnica.cuentas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador REST para reportes
 * F4: Reporte de estado de cuenta
 */
@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {
    
    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);
    
    private final ReporteService reporteService;
    
    /**
     * Generar reporte de estado de cuenta - F4
     * GET /api/v1/reportes?clienteId={clienteId}&fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD
     */
    @GetMapping
    public ResponseEntity<EstadoCuentaDTO> generarReporte(
        @RequestParam Long clienteId,
        @RequestParam(required = false) LocalDate fechaInicio,
        @RequestParam(required = false) LocalDate fechaFin) {
        
        log.info("GET /reportes - Generando reporte para cliente {} entre {} y {}", 
            clienteId, fechaInicio, fechaFin);
        
        // Si no se especifican fechas, usar el mes actual
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }
        
        EstadoCuentaDTO reporte = reporteService.generarEstadoCuenta(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}

