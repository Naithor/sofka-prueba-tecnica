package com.naithor.sofkapruebatecnica.cuentas.service;

import com.naithor.sofkapruebatecnica.cuentas.dto.MovimientoDTO;
import com.naithor.sofkapruebatecnica.cuentas.entity.Cuenta;
import com.naithor.sofkapruebatecnica.cuentas.entity.Movimiento;
import com.naithor.sofkapruebatecnica.cuentas.exception.SaldoNoDisponibleException;
import com.naithor.sofkapruebatecnica.cuentas.repository.CuentaRepository;
import com.naithor.sofkapruebatecnica.cuentas.repository.MovimientoRepository;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimientoService Unit Tests")
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private MovimientoService movimientoService;

    private Cuenta cuenta;
    private MovimientoDTO movimientoDTO;
    private Movimiento movimiento;

    @BeforeEach
    void setUp() {
        cuenta = createCuenta();
        movimientoDTO = createMovimientoDTO();
        movimiento = createMovimiento();
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

    private MovimientoDTO createMovimientoDTO() {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setFecha(LocalDate.now());
        dto.setTipoMovimiento("DEPOSITO");
        dto.setValor(BigDecimal.valueOf(600));
        dto.setCuentaId(1L);
        return dto;
    }

    private MovimientoDTO createMovimientoDTO(String tipo, BigDecimal valor) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setFecha(LocalDate.now());
        dto.setTipoMovimiento(tipo);
        dto.setValor(valor);
        dto.setCuentaId(1L);
        return dto;
    }

    private Movimiento createMovimiento() {
        Movimiento m = new Movimiento();
        m.setId(1L);
        m.setFecha(LocalDate.now());
        m.setTipoMovimiento("DEPOSITO");
        m.setValor(BigDecimal.valueOf(600));
        m.setSaldo(BigDecimal.valueOf(2600));
        m.setCuenta(cuenta);
        m.setCuentaNumero("478758");
        return m;
    }

    private Movimiento createMovimiento(Long id, String tipo, BigDecimal valor, BigDecimal saldo, Cuenta cuenta) {
        Movimiento m = new Movimiento();
        m.setId(id);
        m.setFecha(LocalDate.now());
        m.setTipoMovimiento(tipo);
        m.setValor(valor);
        m.setSaldo(saldo);
        m.setCuenta(cuenta);
        m.setCuentaNumero(cuenta.getNumeroCuenta());
        return m;
    }

    @Nested
    @DisplayName("Registrar Movimiento Tests")
    class RegistrarMovimientoTests {

        @Test
        @DisplayName("Debe registrar un depósito exitosamente")
        void debeRegistrarDepositoExitosamente() {
            when(cuentaRepository.findByIdWithLock(1L)).thenReturn(Optional.of(cuenta));
            when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimiento);
            when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

            MovimientoDTO resultado = movimientoService.registrarMovimiento(movimientoDTO);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getTipoMovimiento()).isEqualTo("DEPOSITO");
            verify(movimientoRepository).save(any(Movimiento.class));
            verify(cuentaRepository).save(any(Cuenta.class));
        }

        @Test
        @DisplayName("Debe registrar un retiro exitosamente y actualizar saldo")
        void debeRegistrarRetiroExitosamente() {
            MovimientoDTO retiroDTO = createMovimientoDTO("RETIRO", BigDecimal.valueOf(500));
            Movimiento retiroMovimiento = createMovimiento(2L, "RETIRO", BigDecimal.valueOf(500), BigDecimal.valueOf(1500), cuenta);

            when(cuentaRepository.findByIdWithLock(1L)).thenReturn(Optional.of(cuenta));
            when(movimientoRepository.save(any(Movimiento.class))).thenReturn(retiroMovimiento);
            when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

            MovimientoDTO resultado = movimientoService.registrarMovimiento(retiroDTO);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getTipoMovimiento()).isEqualTo("RETIRO");
            verify(movimientoRepository).save(any(Movimiento.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando cuenta no existe")
        void debeLanzarExcepcionCuandoCuentaNoExiste() {
            when(cuentaRepository.findByIdWithLock(999L)).thenReturn(Optional.empty());

            MovimientoDTO dto = createMovimientoDTO();
            dto.setCuentaId(999L);

            assertThatThrownBy(() -> movimientoService.registrarMovimiento(dto))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("CUENTA_NO_ENCONTRADA");
                });

            verify(movimientoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando saldo insuficiente para retiro")
        void debeLanzarExcepcionCuandoSaldoInsuficiente() {
            MovimientoDTO retiroDTO = createMovimientoDTO("RETIRO", BigDecimal.valueOf(5000));

            when(cuentaRepository.findByIdWithLock(1L)).thenReturn(Optional.of(cuenta));

            assertThatThrownBy(() -> movimientoService.registrarMovimiento(retiroDTO))
                .isInstanceOf(SaldoNoDisponibleException.class)
                .satisfies(ex -> {
                    SaldoNoDisponibleException bex = (SaldoNoDisponibleException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("SALDO_NO_DISPONIBLE");
                });

            verify(movimientoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Obtener Movimiento Tests")
    class ObtenerMovimientoTests {

        @Test
        @DisplayName("Debe obtener todos los movimientos")
        void debeObtenerTodosLosMovimientos() {
            Movimiento mov1 = createMovimiento(1L, "DEPOSITO", BigDecimal.valueOf(600), BigDecimal.valueOf(2600), cuenta);
            Movimiento mov2 = createMovimiento(2L, "RETIRO", BigDecimal.valueOf(100), BigDecimal.valueOf(2500), cuenta);
            when(movimientoRepository.findAll()).thenReturn(Arrays.asList(mov1, mov2));

            List<MovimientoDTO> resultado = movimientoService.obtenerTodos();

            assertThat(resultado).hasSize(2);
        }

        @Test
        @DisplayName("Debe obtener movimiento por ID")
        void debeObtenerMovimientoPorId() {
            when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimiento));

            MovimientoDTO resultado = movimientoService.obtenerPorId(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando movimiento no existe")
        void debeLanzarExcepcionCuandoMovimientoNoExiste() {
            when(movimientoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movimientoService.obtenerPorId(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("MOVIMIENTO_NO_ENCONTRADO");
                });
        }

        @Test
        @DisplayName("Debe obtener movimientos por cuenta ID")
        void debeObtenerMovimientosPorCuentaId() {
            Movimiento mov1 = createMovimiento(1L, "DEPOSITO", BigDecimal.valueOf(600), BigDecimal.valueOf(2600), cuenta);
            when(movimientoRepository.findByCuentaId(1L)).thenReturn(Arrays.asList(mov1));

            List<MovimientoDTO> resultado = movimientoService.obtenerPorCuenta(1L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getCuentaId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Debe obtener movimientos por cuenta y rango de fechas")
        void debeObtenerMovimientosPorCuentaYFechas() {
            LocalDate fechaInicio = LocalDate.now().minusDays(7);
            LocalDate fechaFin = LocalDate.now();
            Movimiento mov1 = createMovimiento(1L, "DEPOSITO", BigDecimal.valueOf(600), BigDecimal.valueOf(2600), cuenta);
            when(movimientoRepository.findByCuentaIdAndFechaBetween(1L, fechaInicio, fechaFin))
                .thenReturn(Arrays.asList(mov1));

            List<MovimientoDTO> resultado = movimientoService.obtenerPorCuentaYFechas(1L, fechaInicio, fechaFin);

            assertThat(resultado).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Eliminar Movimiento Tests")
    class EliminarMovimientoTests {

        @Test
        @DisplayName("Debe eliminar un movimiento exitosamente")
        void debeEliminarMovimientoExitosamente() {
            when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimiento));

            movimientoService.eliminarMovimiento(1L);

            verify(movimientoRepository).delete(movimiento);
        }

        @Test
        @DisplayName("Debe lanzar excepción al eliminar movimiento inexistente")
        void debeLanzarExcepcionAlEliminarMovimientoInexistente() {
            when(movimientoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movimientoService.eliminarMovimiento(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bex = (BusinessException) ex;
                    assertThat(bex.getErrorCode()).isEqualTo("MOVIMIENTO_NO_ENCONTRADO");
                });

            verify(movimientoRepository, never()).delete(any());
        }
    }
}
