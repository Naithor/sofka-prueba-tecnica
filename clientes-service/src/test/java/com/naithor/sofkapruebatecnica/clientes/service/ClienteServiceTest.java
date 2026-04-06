package com.naithor.sofkapruebatecnica.clientes.service;

import com.naithor.sofkapruebatecnica.clientes.dto.ClienteDTO;
import com.naithor.sofkapruebatecnica.clientes.entity.Cliente;
import com.naithor.sofkapruebatecnica.clientes.repository.ClienteRepository;
import com.naithor.sofkapruebatecnica.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService Unit Tests")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteDTO clienteDTO;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        clienteDTO = createClienteDTO();
        cliente = createCliente();
    }

    private ClienteDTO createClienteDTO() {
        ClienteDTO dto = new ClienteDTO();
        dto.setNombre("Jose Lema");
        dto.setGenero("Masculino");
        dto.setEdad(25);
        dto.setIdentificacion("1234567890");
        dto.setDireccion("Otavalo sn y principal");
        dto.setTelefono("0982547851");
        dto.setClienteId("JLEMA001");
        dto.setContrasena("1234");
        dto.setEstado(true);
        return dto;
    }

    private Cliente createCliente() {
        Cliente c = new Cliente();
        c.setId(1L);
        c.setNombre("Jose Lema");
        c.setGenero("Masculino");
        c.setEdad(25);
        c.setIdentificacion("1234567890");
        c.setDireccion("Otavalo sn y principal");
        c.setTelefono("0982547851");
        c.setClienteId("JLEMA001");
        c.setContrasena("1234");
        c.setEstado(true);
        return c;
    }

    private Cliente createCliente(Long id, String clienteId, String identificacion) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setNombre("Jose Lema");
        c.setGenero("Masculino");
        c.setEdad(25);
        c.setIdentificacion(identificacion);
        c.setDireccion("Otavalo sn y principal");
        c.setTelefono("0982547851");
        c.setClienteId(clienteId);
        c.setContrasena("1234");
        c.setEstado(true);
        return c;
    }

    @Nested
    @DisplayName("Crear Cliente Tests")
    class CrearClienteTests {

        @Test
        @DisplayName("Debe crear un cliente exitosamente")
        void debeCrearClienteExitosamente() {
            when(clienteRepository.findByClienteId(anyString())).thenReturn(Optional.empty());
            when(clienteRepository.findByIdentificacion(anyString())).thenReturn(Optional.empty());
            when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

            ClienteDTO resultado = clienteService.crearCliente(clienteDTO);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Jose Lema");
            assertThat(resultado.getClienteId()).isEqualTo("JLEMA001");
            verify(clienteRepository).save(any(Cliente.class));
            verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Object.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando clienteId ya existe")
        void debeLanzarExcepcionCuandoClienteIdYaExiste() {
            when(clienteRepository.findByClienteId(clienteDTO.getClienteId()))
                .thenReturn(Optional.of(cliente));

            assertThatThrownBy(() -> clienteService.crearCliente(clienteDTO))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("CLIENTE_DUPLICADO");
                    assertThat(bex.getMessage()).contains("JLEMA001");
                });

            verify(clienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando identificación ya existe")
        void debeLanzarExcepcionCuandoIdentificacionYaExiste() {
            when(clienteRepository.findByClienteId(anyString())).thenReturn(Optional.empty());
            when(clienteRepository.findByIdentificacion(clienteDTO.getIdentificacion()))
                .thenReturn(Optional.of(cliente));

            assertThatThrownBy(() -> clienteService.crearCliente(clienteDTO))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("IDENTIFICACION_DUPLICADA");
                });

            verify(clienteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Obtener Cliente Tests")
    class ObtenerClienteTests {

        @Test
        @DisplayName("Debe obtener todos los clientes")
        void debeObtenerTodosLosClientes() {
            Cliente cliente2 = createCliente(2L, "MLOPE001", "9876543210");
            when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente, cliente2));

            List<ClienteDTO> resultado = clienteService.obtenerTodos();

            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).getNombre()).isEqualTo("Jose Lema");
            assertThat(resultado.get(1).getNombre()).isEqualTo("Jose Lema");
        }

        @Test
        @DisplayName("Debe obtener cliente por ID")
        void debeObtenerClientePorId() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

            ClienteDTO resultado = clienteService.obtenerPorId(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Jose Lema");
            assertThat(resultado.getClienteId()).isEqualTo("JLEMA001");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando cliente no existe")
        void debeLanzarExcepcionCuandoClienteNoExiste() {
            when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.obtenerPorId(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("CLIENTE_NO_ENCONTRADO");
                });
        }
    }

    @Nested
    @DisplayName("Actualizar Cliente Tests")
    class ActualizarClienteTests {

        @Test
        @DisplayName("Debe actualizar un cliente exitosamente")
        void debeActualizarClienteExitosamente() {
            ClienteDTO clienteActualizado = createClienteDTO();
            clienteActualizado.setNombre("Jose Lema Actualizado");
            
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

            ClienteDTO resultado = clienteService.actualizarCliente(1L, clienteActualizado);

            assertThat(resultado).isNotNull();
            verify(clienteRepository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción al actualizar cliente inexistente")
        void debeLanzarExcepcionAlActualizarClienteInexistente() {
            when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.actualizarCliente(999L, clienteDTO))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("CLIENTE_NO_ENCONTRADO");
                });
        }
    }

    @Nested
    @DisplayName("Eliminar Cliente Tests")
    class EliminarClienteTests {

        @Test
        @DisplayName("Debe eliminar un cliente exitosamente")
        void debeEliminarClienteExitosamente() {
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

            clienteService.eliminarCliente(1L);

            verify(clienteRepository).delete(cliente);
            verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Object.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción al eliminar cliente inexistente")
        void debeLanzarExcepcionAlEliminarClienteInexistente() {
            when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.eliminarCliente(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("CLIENTE_NO_ENCONTRADO");
                });

            verify(clienteRepository, never()).delete(any());
        }
    }
}
