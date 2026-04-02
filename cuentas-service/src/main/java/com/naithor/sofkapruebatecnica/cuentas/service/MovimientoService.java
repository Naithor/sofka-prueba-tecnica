package com.naithor.sofkapruebatecnica.cuentas.service;

import com.naithor.sofkapruebatecnica.cuentas.dto.MovimientoDTO;
import com.naithor.sofkapruebatecnica.cuentas.entity.Cuenta;
import com.naithor.sofkapruebatecnica.cuentas.entity.Movimiento;
import com.naithor.sofkapruebatecnica.cuentas.exception.SaldoNoDisponibleException;
import com.naithor.sofkapruebatecnica.cuentas.repository.CuentaRepository;
import com.naithor.sofkapruebatecnica.cuentas.repository.MovimientoRepository;
import com.naithor.sofkapruebatecnica.shared.enums.TipoMovimiento;
import com.naithor.sofkapruebatecnica.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MovimientoService {
    
    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    
    public MovimientoDTO registrarMovimiento(MovimientoDTO movimientoDTO) {
        String correlationId = java.util.UUID.randomUUID().toString();
        log.info("correlationId={} - Registrando movimiento tipo={} cuenta={} valor={}", 
            correlationId, movimientoDTO.getTipoMovimiento(), movimientoDTO.getCuentaId(), movimientoDTO.getValor());
        
        validarMovimiento(movimientoDTO);
        
        Cuenta cuenta = cuentaRepository.findByIdWithLock(movimientoDTO.getCuentaId())
            .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", 
                "No existe cuenta con ID: " + movimientoDTO.getCuentaId()));
        
        validarCuentaActiva(cuenta);
        validarFechaMovimiento(movimientoDTO.getFecha());
        
        TipoMovimiento tipoMovimiento = TipoMovimiento.fromString(movimientoDTO.getTipoMovimiento());
        BigDecimal nuevoSaldo = cuenta.getSaldoDisponible();
        
        if (tipoMovimiento == TipoMovimiento.DEPOSITO) {
            nuevoSaldo = nuevoSaldo.add(movimientoDTO.getValor());
            log.info("correlationId={} - Depósito procesado: {}", correlationId, movimientoDTO.getValor());
        } else if (tipoMovimiento == TipoMovimiento.RETIRO) {
            if (cuenta.getSaldoDisponible().compareTo(movimientoDTO.getValor()) < 0) {
                log.warn("correlationId={} - Saldo no disponible. Saldo actual: {}, Retiro solicitado: {}", 
                    correlationId, cuenta.getSaldoDisponible(), movimientoDTO.getValor());
                throw new SaldoNoDisponibleException(
                    "Saldo no disponible para realizar el retiro de " + movimientoDTO.getValor());
            }
            nuevoSaldo = nuevoSaldo.subtract(movimientoDTO.getValor());
            log.info("correlationId={} - Retiro procesado: {}", correlationId, movimientoDTO.getValor());
        }
        
        Movimiento movimiento = Movimiento.builder()
                .fecha(movimientoDTO.getFecha() != null ? movimientoDTO.getFecha() : LocalDate.now())
                .tipoMovimiento(tipoMovimiento.name())
                .valor(movimientoDTO.getValor())
                .saldo(nuevoSaldo)
                .cuenta(cuenta)
                .cuentaNumero(cuenta.getNumeroCuenta())
                .build();
        
        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);
        
        log.info("correlationId={} - Movimiento registrado id={} nuevoSaldo={}", 
            correlationId, movimientoGuardado.getId(), nuevoSaldo);
        
        return mapToDTO(movimientoGuardado);
    }
    
    private void validarMovimiento(MovimientoDTO movimientoDTO) {
        if (movimientoDTO.getValor() == null || movimientoDTO.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("VALOR_INVALIDO", "El valor debe ser mayor a cero");
        }
        if (movimientoDTO.getValor().compareTo(new BigDecimal("999999999.99")) > 0) {
            throw new BusinessException("VALOR_EXCEDE_LIMITE", "El valor excede el límite permitido");
        }
    }
    
    private void validarCuentaActiva(Cuenta cuenta) {
        if (!cuenta.getEstado()) {
            throw new BusinessException("CUENTA_INACTIVA", "La cuenta no está activa");
        }
    }
    
    private void validarFechaMovimiento(LocalDate fecha) {
        if (fecha != null && fecha.isAfter(LocalDate.now())) {
            throw new BusinessException("FECHA_INVALIDA", "La fecha del movimiento no puede ser futura");
        }
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerTodos() {
        log.info("Obteniendo todos los movimientos");
        return movimientoRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public MovimientoDTO obtenerPorId(Long id) {
        log.info("Obteniendo movimiento con ID: {}", id);
        Movimiento movimiento = movimientoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("MOVIMIENTO_NO_ENCONTRADO", 
                "No existe movimiento con ID: " + id));
        return mapToDTO(movimiento);
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerPorCuenta(Long cuentaId) {
        log.info("Obteniendo movimientos de la cuenta: {}", cuentaId);
        return movimientoRepository.findByCuentaId(cuentaId)
            .stream()
            .map(this::mapToDTO)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public List<MovimientoDTO> obtenerPorCuentaYFechas(Long cuentaId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Obteniendo movimientos de cuenta {} entre {} y {}", cuentaId, fechaInicio, fechaFin);
        return movimientoRepository.findByCuentaIdAndFechaBetween(cuentaId, fechaInicio, fechaFin)
            .stream()
            .map(this::mapToDTO)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public List<Movimiento> obtenerMovimientosCliente(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Obteniendo movimientos de cliente {} entre {} y {}", clienteId, fechaInicio, fechaFin);
        return movimientoRepository.findByClienteAndDateRange(clienteId, fechaInicio, fechaFin);
    }
    
    public void eliminarMovimiento(Long id) {
        log.info("Eliminando movimiento con ID: {}", id);
        
        Movimiento movimiento = movimientoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("MOVIMIENTO_NO_ENCONTRADO", 
                "No existe movimiento con ID: " + id));
        
        movimientoRepository.delete(movimiento);
        log.info("Movimiento eliminado con ID: {}", id);
    }
    
    private MovimientoDTO mapToDTO(Movimiento movimiento) {
        return MovimientoDTO.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldo(movimiento.getSaldo())
                .cuentaId(movimiento.getCuenta().getId())
                .cuentaNumero(movimiento.getCuentaNumero())
                .build();
    }
}
