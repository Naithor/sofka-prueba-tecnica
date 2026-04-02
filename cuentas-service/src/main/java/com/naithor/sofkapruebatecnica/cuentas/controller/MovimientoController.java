package com.naithor.sofkapruebatecnica.cuentas.controller;

import com.naithor.sofkapruebatecnica.cuentas.dto.MovimientoDTO;
import com.naithor.sofkapruebatecnica.cuentas.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gestión de movimientos
 * F2: Registro de movimientos
 * F3: Validación de saldo disponible
 */
@RestController
@RequestMapping("/api/v1/movimientos")
@RequiredArgsConstructor
public class MovimientoController {
    
    private static final Logger log = LoggerFactory.getLogger(MovimientoController.class);
    
    private final MovimientoService movimientoService;
    
    /**
     * Registrar un movimiento (depósito o retiro) - F2, F3
     * POST /api/v1/movimientos
     */
    @PostMapping
    public ResponseEntity<MovimientoDTO> registrarMovimiento(@Valid @RequestBody MovimientoDTO movimientoDTO) {
        log.info("POST /movimientos - Registrando movimiento de tipo {}", movimientoDTO.getTipoMovimiento());
        MovimientoDTO movimientoRegistrado = movimientoService.registrarMovimiento(movimientoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoRegistrado);
    }
    
    /**
     * Obtener todos los movimientos - F1 (CRUD Read)
     * GET /api/v1/movimientos
     */
    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> obtenerTodos() {
        log.info("GET /movimientos - Obteniendo todos los movimientos");
        List<MovimientoDTO> movimientos = movimientoService.obtenerTodos();
        return ResponseEntity.ok(movimientos);
    }
    
    /**
     * Obtener movimiento por ID
     * GET /api/v1/movimientos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /movimientos/{} - Obteniendo movimiento", id);
        MovimientoDTO movimiento = movimientoService.obtenerPorId(id);
        return ResponseEntity.ok(movimiento);
    }
    
    /**
     * Obtener movimientos de una cuenta
     * GET /api/v1/movimientos/cuenta/{cuentaId}
     */
    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorCuenta(@PathVariable Long cuentaId) {
        log.info("GET /movimientos/cuenta/{} - Obteniendo movimientos", cuentaId);
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorCuenta(cuentaId);
        return ResponseEntity.ok(movimientos);
    }
    
    /**
     * Obtener movimientos de una cuenta por rango de fechas
     * GET /api/v1/movimientos/cuenta/{cuentaId}/fechas?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD
     */
    @GetMapping("/cuenta/{cuentaId}/fechas")
    public ResponseEntity<List<MovimientoDTO>> obtenerPorCuentaYFechas(
        @PathVariable Long cuentaId,
        @RequestParam LocalDate fechaInicio,
        @RequestParam LocalDate fechaFin) {
        log.info("GET /movimientos/cuenta/{}/fechas - Obteniendo movimientos entre fechas", cuentaId);
        List<MovimientoDTO> movimientos = movimientoService.obtenerPorCuentaYFechas(cuentaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(movimientos);
    }
    
    /**
     * Eliminar movimiento - F1 (CRUD Delete)
     * DELETE /api/v1/movimientos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        log.info("DELETE /movimientos/{} - Eliminando movimiento", id);
        movimientoService.eliminarMovimiento(id);
        return ResponseEntity.noContent().build();
    }
}

