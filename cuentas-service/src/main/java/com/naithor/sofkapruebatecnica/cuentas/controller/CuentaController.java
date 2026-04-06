package com.naithor.sofkapruebatecnica.cuentas.controller;

import com.naithor.sofkapruebatecnica.cuentas.dto.CuentaDTO;
import com.naithor.sofkapruebatecnica.cuentas.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
public class CuentaController {
    
    private static final Logger log = LoggerFactory.getLogger(CuentaController.class);
    
    private final CuentaService cuentaService;
    
    @PostMapping
    public ResponseEntity<CuentaDTO> crearCuenta(@Valid @RequestBody CuentaDTO cuentaDTO) {
        log.info("POST /cuentas - Creando nueva cuenta");
        CuentaDTO cuentaCreada = cuentaService.crearCuenta(cuentaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaCreada);
    }
    
    @GetMapping
    public ResponseEntity<List<CuentaDTO>> obtenerTodas() {
        log.info("GET /cuentas - Obteniendo todas las cuentas");
        List<CuentaDTO> cuentas = cuentaService.obtenerTodas();
        return ResponseEntity.ok(cuentas);
    }
    
    @GetMapping("/all")
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

