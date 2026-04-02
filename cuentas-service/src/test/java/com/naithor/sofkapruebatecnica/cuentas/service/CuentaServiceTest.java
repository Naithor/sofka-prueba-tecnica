package com.naithor.sofkapruebatecnica.cuentas.service;

import com.naithor.sofkapruebatecnica.cuentas.dto.CuentaDTO;
import com.naithor.sofkapruebatecnica.cuentas.entity.Cuenta;
import com.naithor.sofkapruebatecnica.cuentas.repository.CuentaRepository;
import com.naithor.sofkapruebatecnica.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CuentaService Unit Tests")
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private CuentaService cuentaService;

    private CuentaDTO cuentaDTO;
    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        cuentaDTO = createCuentaDTO();
        cuenta = createCuenta();
    }

    private CuentaDTO createCuentaDTO() {
        CuentaDTO dto = new CuentaDTO();
        dto.setNumeroCuenta("478758");
        dto.setTipoCuenta("AHORROS");
        dto.setSaldoInicial(BigDecimal.valueOf(2000));
        dto.setSaldoDisponible(BigDecimal.valueOf(2000));
        dto.setEstado(true);
        dto.setClienteId(1L);
        dto.setClienteIdentificacion("1234567890");
        return dto;
    }

    private Cuenta createCuenta() {
        Cuenta c = new Cuenta();
        c.setId(1L);
        c.setNumeroCuenta("478758");
        c.setTipoCuenta("AHORROS");
        c.setSaldoInicial(BigDecimal.valueOf(2000));
        c.setSaldoDisponible(BigDecimal.valueOf(2000));
        c.setEstado(true);
        c.setClienteId(1L);
        c.setClienteIdentificacion("1234567890");
        return c;
    }

    private Cuenta createCuenta(Long id, String numeroCuenta, BigDecimal saldoDisponible) {
        Cuenta c = new Cuenta();
        c.setId(id);
        c.setNumeroCuenta(numeroCuenta);
        c.setTipoCuenta("AHORROS");
        c.setSaldoInicial(saldoDisponible);
        c.setSaldoDisponible(saldoDisponible);
        c.setEstado(true);
        c.setClienteId(1L);
        c.setClienteIdentificacion("1234567890");
        return c;
    }

    @Nested
    @DisplayName("Crear Cuenta Tests")
    class CrearCuentaTests {

        @Test
        @DisplayName("Debe crear una cuenta exitosamente")
        void debeCrearCuentaExitosamente() {
            when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.empty());
            when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

            CuentaDTO resultado = cuentaService.crearCuenta(cuentaDTO);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getNumeroCuenta()).isEqualTo("478758");
            verify(cuentaRepository).save(any(Cuenta.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando número de cuenta ya existe")
        void debeLanzarExcepcionCuandoNumeroDeCuentaYaExiste() {
            when(cuentaRepository.findByNumeroCuenta(cuentaDTO.getNumeroCuenta()))
                .thenReturn(Optional.of(cuenta));

            assertThatThrownBy(() -> cuentaService.crearCuenta(cuentaDTO))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("CUENTA_DUPLICADA");
                });

            verify(cuentaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Obtener Cuenta Tests")
    class ObtenerCuentaTests {

        @Test
        @DisplayName("Debe obtener todas las cuentas")
        void debeObtenerTodasLasCuentas() {
            Cuenta cuenta2 = createCuenta(2L, "478759", BigDecimal.valueOf(3000));
            when(cuentaRepository.findAll()).thenReturn(Arrays.asList(cuenta, cuenta2));

            List<CuentaDTO> resultado = cuentaService.obtenerTodas();

            assertThat(resultado).hasSize(2);
        }

        @Test
        @DisplayName("Debe obtener cuenta por ID")
        void debeObtenerCuentaPorId() {
            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

            CuentaDTO resultado = cuentaService.obtenerPorId(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getNumeroCuenta()).isEqualTo("478758");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando cuenta no existe")
        void debeLanzarExcepcionCuandoCuentaNoExiste() {
            when(cuentaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.obtenerPorId(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("CUENTA_NO_ENCONTRADA");
                });
        }

        @Test
        @DisplayName("Debe obtener cuentas por cliente ID")
        void debeObtenerCuentasPorClienteId() {
            when(cuentaRepository.findByClienteId(1L)).thenReturn(Arrays.asList(cuenta));

            List<CuentaDTO> resultado = cuentaService.obtenerPorClienteId(1L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getClienteId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("Actualizar Cuenta Tests")
    class ActualizarCuentaTests {

        @Test
        @DisplayName("Debe actualizar una cuenta exitosamente")
        void debeActualizarCuentaExitosamente() {
            CuentaDTO cuentaActualizada = createCuentaDTO();
            cuentaActualizada.setEstado(false);

            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
            when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

            CuentaDTO resultado = cuentaService.actualizarCuenta(1L, cuentaActualizada);

            assertThat(resultado).isNotNull();
            verify(cuentaRepository).save(any(Cuenta.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción al actualizar cuenta inexistente")
        void debeLanzarExcepcionAlActualizarCuentaInexistente() {
            when(cuentaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.actualizarCuenta(999L, cuentaDTO))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("CUENTA_NO_ENCONTRADA");
                });
        }
    }

    @Nested
    @DisplayName("Eliminar Cuenta Tests")
    class EliminarCuentaTests {

        @Test
        @DisplayName("Debe eliminar una cuenta exitosamente")
        void debeEliminarCuentaExitosamente() {
            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

            cuentaService.eliminarCuenta(1L);

            verify(cuentaRepository).delete(cuenta);
        }

        @Test
        @DisplayName("Debe lanzar excepción al eliminar cuenta inexistente")
        void debeLanzarExcepcionAlEliminarCuentaInexistente() {
            when(cuentaRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.eliminarCuenta(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("CUENTA_NO_ENCONTRADA");
                });

            verify(cuentaRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Saldo Tests")
    class SaldoTests {

        @Test
        @DisplayName("Debe actualizar saldo de cuenta")
        void debeActualizarSaldoDeCuenta() {
            BigDecimal nuevoSaldo = BigDecimal.valueOf(3000);
            when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
            when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

            cuentaService.actualizarSaldo(1L, nuevoSaldo);

            verify(cuentaRepository).save(any(Cuenta.class));
        }
    }
}
