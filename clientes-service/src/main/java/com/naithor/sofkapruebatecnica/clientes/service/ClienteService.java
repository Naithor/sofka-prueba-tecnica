package com.naithor.sofkapruebatecnica.clientes.service;

import com.naithor.sofkapruebatecnica.clientes.dto.ClienteDTO;
import com.naithor.sofkapruebatecnica.clientes.entity.Cliente;
import com.naithor.sofkapruebatecnica.clientes.event.ClienteCreadoEvent;
import com.naithor.sofkapruebatecnica.clientes.event.ClienteEliminadoEvent;
import com.naithor.sofkapruebatecnica.clientes.repository.ClienteRepository;
import com.naithor.sofkapruebatecnica.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClienteService {
    
    private static final String EXCHANGE_NAME = "clientes-exchange";
    private static final String CLIENTE_CREADO_ROUTING_KEY = "cliente.creado";
    private static final String CLIENTE_ELIMINADO_ROUTING_KEY = "cliente.eliminado";
    
    private final ClienteRepository clienteRepository;
    private final RabbitTemplate rabbitTemplate;
    
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        log.info("Creando cliente: {}", clienteDTO.getClienteId());
        
        if (clienteRepository.findByClienteId(clienteDTO.getClienteId()).isPresent()) {
            throw new BusinessException("CLIENTE_DUPLICADO", 
                "Ya existe un cliente con el ID: " + clienteDTO.getClienteId());
        }
        
        if (clienteRepository.findByIdentificacion(clienteDTO.getIdentificacion()).isPresent()) {
            throw new BusinessException("IDENTIFICACION_DUPLICADA", 
                "Ya existe un cliente con la identificacion: " + clienteDTO.getIdentificacion());
        }
        
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setGenero(clienteDTO.getGenero());
        cliente.setEdad(clienteDTO.getEdad());
        cliente.setIdentificacion(clienteDTO.getIdentificacion());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setClienteId(clienteDTO.getClienteId());
        cliente.setContrasena(clienteDTO.getContrasena());
        cliente.setEstado(clienteDTO.getEstado() != null ? clienteDTO.getEstado() : true);
        
        Cliente clienteGuardado = clienteRepository.save(cliente);
        log.info("Cliente creado con ID: {}", clienteGuardado.getId());
        
        ClienteCreadoEvent evento = new ClienteCreadoEvent();
        evento.setEventId(UUID.randomUUID().toString());
        evento.setClienteId(clienteGuardado.getId());
        evento.setNombre(clienteGuardado.getNombre());
        evento.setIdentificacion(clienteGuardado.getIdentificacion());
        
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, CLIENTE_CREADO_ROUTING_KEY, evento);
        log.info("Evento ClienteCreado publicado");
        
        return mapToDTO(clienteGuardado);
    }
    
    @Transactional(readOnly = true)
    public List<ClienteDTO> obtenerTodos() {
        log.info("Obteniendo todos los clientes");
        return clienteRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public ClienteDTO obtenerPorId(Long id) {
        log.info("Obteniendo cliente con ID: {}", id);
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new BusinessException("CLIENTE_NO_ENCONTRADO", 
                "No existe cliente con ID: " + id));
        return mapToDTO(cliente);
    }
    
    public ClienteDTO actualizarCliente(Long id, ClienteDTO clienteDTO) {
        log.info("Actualizando cliente con ID: {}", id);
        
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new BusinessException("CLIENTE_NO_ENCONTRADO", 
                "No existe cliente con ID: " + id));
        
        if (!cliente.getClienteId().equals(clienteDTO.getClienteId()) &&
            clienteRepository.findByClienteId(clienteDTO.getClienteId()).isPresent()) {
            throw new BusinessException("CLIENTE_DUPLICADO", 
                "Ya existe un cliente con el ID: " + clienteDTO.getClienteId());
        }
        
        if (!cliente.getIdentificacion().equals(clienteDTO.getIdentificacion()) &&
            clienteRepository.findByIdentificacion(clienteDTO.getIdentificacion()).isPresent()) {
            throw new BusinessException("IDENTIFICACION_DUPLICADA", 
                "Ya existe un cliente con la identificacion: " + clienteDTO.getIdentificacion());
        }
        
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setGenero(clienteDTO.getGenero());
        cliente.setEdad(clienteDTO.getEdad());
        cliente.setIdentificacion(clienteDTO.getIdentificacion());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setClienteId(clienteDTO.getClienteId());
        cliente.setContrasena(clienteDTO.getContrasena());
        cliente.setEstado(clienteDTO.getEstado());
        
        Cliente clienteActualizado = clienteRepository.save(cliente);
        log.info("Cliente actualizado con ID: {}", clienteActualizado.getId());
        
        return mapToDTO(clienteActualizado);
    }
    
    public void eliminarCliente(Long id) {
        log.info("Eliminando cliente con ID: {}", id);
        
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new BusinessException("CLIENTE_NO_ENCONTRADO", 
                "No existe cliente con ID: " + id));
        
        clienteRepository.delete(cliente);
        log.info("Cliente eliminado con ID: {}", id);
        
        ClienteEliminadoEvent evento = new ClienteEliminadoEvent();
        evento.setEventId(UUID.randomUUID().toString());
        evento.setClienteId(id);
        evento.setIdentificacion(cliente.getIdentificacion());
        
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, CLIENTE_ELIMINADO_ROUTING_KEY, evento);
        log.info("Evento ClienteEliminado publicado");
    }
    
    private ClienteDTO mapToDTO(Cliente cliente) {
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .clienteId(cliente.getClienteId())
                .contrasena(cliente.getContrasena())
                .estado(cliente.getEstado())
                .build();
    }
}
