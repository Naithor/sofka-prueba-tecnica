package com.naithor.sofkapruebatecnica.cuentas.service;

import com.naithor.sofkapruebatecnica.cuentas.dto.CuentaDTO;
import com.naithor.sofkapruebatecnica.cuentas.entity.Cuenta;
import com.naithor.sofkapruebatecnica.cuentas.repository.CuentaRepository;
import com.naithor.sofkapruebatecnica.shared.dto.PageResponse;
import com.naithor.sofkapruebatecnica.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CuentaService {
    
    private final CuentaRepository cuentaRepository;
    
    public CuentaDTO crearCuenta(CuentaDTO cuentaDTO) {
        String correlationId = java.util.UUID.randomUUID().toString();
        log.info("correlationId={} - Creando cuenta: {}", correlationId, cuentaDTO.getNumeroCuenta());
        
        if (cuentaRepository.findByNumeroCuenta(cuentaDTO.getNumeroCuenta()).isPresent()) {
            throw new BusinessException("CUENTA_DUPLICADA", 
                "Ya existe una cuenta con el número: " + cuentaDTO.getNumeroCuenta());
        }
        
        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(cuentaDTO.getNumeroCuenta())
                .tipoCuenta(cuentaDTO.getTipoCuenta())
                .saldoInicial(cuentaDTO.getSaldoInicial())
                .saldoDisponible(cuentaDTO.getSaldoInicial())
                .estado(cuentaDTO.getEstado() != null ? cuentaDTO.getEstado() : true)
                .clienteId(cuentaDTO.getClienteId())
                .clienteIdentificacion(cuentaDTO.getClienteIdentificacion())
                .build();
        
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        log.info("correlationId={} - Cuenta creada con ID: {}", correlationId, cuentaGuardada.getId());
        
        return mapToDTO(cuentaGuardada);
    }
    
    @Transactional(readOnly = true)
    public PageResponse<CuentaDTO> obtenerTodas(int page, int size) {
        log.info("Obteniendo cuentas paginadas - page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        Page<Cuenta> cuentasPage = cuentaRepository.findAll(pageable);
        return PageResponse.of(cuentasPage.map(this::mapToDTO));
    }
    
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerTodas() {
        log.info("Obteniendo todas las cuentas");
        return cuentaRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public CuentaDTO obtenerPorId(Long id) {
        log.info("Obteniendo cuenta con ID: {}", id);
        Cuenta cuenta = cuentaRepository.findById(id)
            .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", 
                "No existe cuenta con ID: " + id));
        return mapToDTO(cuenta);
    }
    
    @Transactional(readOnly = true)
    public List<CuentaDTO> obtenerPorClienteId(Long clienteId) {
        log.info("Obteniendo cuentas del cliente: {}", clienteId);
        return cuentaRepository.findByClienteId(clienteId)
            .stream()
            .map(this::mapToDTO)
            .toList();
    }
    
    public CuentaDTO actualizarCuenta(Long id, CuentaDTO cuentaDTO) {
        log.info("Actualizando cuenta con ID: {}", id);
        
        Cuenta cuenta = cuentaRepository.findById(id)
            .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", 
                "No existe cuenta con ID: " + id));
        
        if (!cuenta.getNumeroCuenta().equals(cuentaDTO.getNumeroCuenta()) &&
            cuentaRepository.findByNumeroCuenta(cuentaDTO.getNumeroCuenta()).isPresent()) {
            throw new BusinessException("CUENTA_DUPLICADA", 
                "Ya existe una cuenta con el número: " + cuentaDTO.getNumeroCuenta());
        }
        
        cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuenta.setEstado(cuentaDTO.getEstado());
        
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        log.info("Cuenta actualizada con ID: {}", cuentaActualizada.getId());
        
        return mapToDTO(cuentaActualizada);
    }
    
    public void eliminarCuenta(Long id) {
        log.info("Eliminando cuenta con ID: {}", id);
        
        Cuenta cuenta = cuentaRepository.findById(id)
            .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", 
                "No existe cuenta con ID: " + id));
        
        cuentaRepository.delete(cuenta);
        log.info("Cuenta eliminada con ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public Cuenta obtenerCuentaPorNumero(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
            .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", 
                "No existe cuenta con número: " + numeroCuenta));
    }
    
    public void actualizarSaldo(Long cuentaId, BigDecimal nuevoSaldo) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
            .orElseThrow(() -> new BusinessException("CUENTA_NO_ENCONTRADA", 
                "No existe cuenta con ID: " + cuentaId));
        
        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }
    
    private CuentaDTO mapToDTO(Cuenta cuenta) {
        return CuentaDTO.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .saldoDisponible(cuenta.getSaldoDisponible())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getClienteId())
                .clienteIdentificacion(cuenta.getClienteIdentificacion())
                .build();
    }
}
