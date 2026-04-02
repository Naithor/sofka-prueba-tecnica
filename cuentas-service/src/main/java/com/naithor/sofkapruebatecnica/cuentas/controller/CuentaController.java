package com.naithor.sofkapruebatecnica.cuentas.controller;

import com.naithor.sofkapruebatecnica.cuentas.dto.CuentaDTO;
import com.naithor.sofkapruebatecnica.cuentas.service.CuentaService;
import com.naithor.sofkapruebatecnica.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para gestión de cuentas
 */
@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "Endpoints para gestión de cuentas bancarias")
public class CuentaController {
    
    private static final Logger log = LoggerFactory.getLogger(CuentaController.class);
    
    private final CuentaService cuentaService;
    
    @PostMapping
    @Operation(summary = "Crear cuenta", description = "Crea una nueva cuenta bancaria")
    public ResponseEntity<CuentaDTO> crearCuenta(@Valid @RequestBody CuentaDTO cuentaDTO) {
        log.info("POST /cuentas - Creando nueva cuenta");
        CuentaDTO cuentaCreada = cuentaService.crearCuenta(cuentaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaCreada);
    }
    
    @GetMapping
    @Operation(summary = "Listar cuentas", description = "Obtiene lista paginada de cuentas")
    public ResponseEntity<PageResponse<CuentaDTO>> obtenerTodas(
            @Parameter(description = "Número de página (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size) {
        log.info("GET /cuentas - Obteniendo cuentas paginadas - page={}, size={}", page, size);
        PageResponse<CuentaDTO> cuentas = cuentaService.obtenerTodas(page, size);
        return ResponseEntity.ok(cuentas);
    }
    
    @GetMapping("/all")
    @Operation(summary = "Listar todas las cuentas", description = "Obtiene todas las cuentas sin paginación")
    public ResponseEntity<List<CuentaDTO>> obtenerTodasSinPaginacion() {
        log.info("GET /cuentas/all - Obteniendo todas las cuentas");
        List<CuentaDTO> cuentas = cuentaService.obtenerTodas();
        return ResponseEntity.ok(cuentas);
    }
    
    /**
     * Obtener cuenta por ID - F1 (CRUD Read)
     * GET /api/v1/cuentas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CuentaDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /cuentas/{} - Obteniendo cuenta", id);
        CuentaDTO cuenta = cuentaService.obtenerPorId(id);
        return ResponseEntity.ok(cuenta);
    }
    
    /**
     * Obtener cuentas por cliente ID
     * GET /api/v1/cuentas/cliente/{clienteId}
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CuentaDTO>> obtenerPorClienteId(@PathVariable Long clienteId) {
        log.info("GET /cuentas/cliente/{} - Obteniendo cuentas del cliente", clienteId);
        List<CuentaDTO> cuentas = cuentaService.obtenerPorClienteId(clienteId);
        return ResponseEntity.ok(cuentas);
    }
    
    /**
     * Actualizar cuenta - F1 (CRUD Update)
     * PUT /api/v1/cuentas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CuentaDTO> actualizarCuenta(
        @PathVariable Long id,
        @Valid @RequestBody CuentaDTO cuentaDTO) {
        log.info("PUT /cuentas/{} - Actualizando cuenta", id);
        CuentaDTO cuentaActualizada = cuentaService.actualizarCuenta(id, cuentaDTO);
        return ResponseEntity.ok(cuentaActualizada);
    }
    
    /**
     * Eliminar cuenta - F1 (CRUD Delete)
     * DELETE /api/v1/cuentas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        log.info("DELETE /cuentas/{} - Eliminando cuenta", id);
        cuentaService.eliminarCuenta(id);
        return ResponseEntity.noContent().build();
    }
}

