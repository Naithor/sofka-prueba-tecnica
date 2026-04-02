package com.naithor.sofkapruebatecnica.clientes.controller;

import com.naithor.sofkapruebatecnica.clientes.dto.ClienteDTO;
import com.naithor.sofkapruebatecnica.clientes.service.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST para gestión de clientes
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
    
    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
    
    private final ClienteService clienteService;
    
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    /**
     * Crear un nuevo cliente - F1 (CRUD Create)
     * POST /api/v1/clientes
     */
    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        log.info("POST /clientes - Creando nuevo cliente");
        ClienteDTO clienteCreado = clienteService.crearCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCreado);
    }
    
    /**
     * Obtener todos los clientes - F1 (CRUD Read)
     * GET /api/v1/clientes
     */
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerTodos() {
        log.info("GET /clientes - Obteniendo todos los clientes");
        List<ClienteDTO> clientes = clienteService.obtenerTodos();
        return ResponseEntity.ok(clientes);
    }
    
    /**
     * Obtener cliente por ID - F1 (CRUD Read)
     * GET /api/v1/clientes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /clientes/{} - Obteniendo cliente", id);
        ClienteDTO cliente = clienteService.obtenerPorId(id);
        return ResponseEntity.ok(cliente);
    }
    
    /**
     * Actualizar cliente - F1 (CRUD Update)
     * PUT /api/v1/clientes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(
        @PathVariable Long id,
        @Valid @RequestBody ClienteDTO clienteDTO) {
        log.info("PUT /clientes/{} - Actualizando cliente", id);
        ClienteDTO clienteActualizado = clienteService.actualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(clienteActualizado);
    }
    
    /**
     * Eliminar cliente - F1 (CRUD Delete)
     * DELETE /api/v1/clientes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        log.info("DELETE /clientes/{} - Eliminando cliente", id);
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}

