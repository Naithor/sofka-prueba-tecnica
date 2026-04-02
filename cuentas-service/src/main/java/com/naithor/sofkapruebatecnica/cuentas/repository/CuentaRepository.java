package com.naithor.sofkapruebatecnica.cuentas.repository;

import com.naithor.sofkapruebatecnica.cuentas.entity.Cuenta;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    
    List<Cuenta> findByClienteId(Long clienteId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("SELECT c FROM Cuenta c WHERE c.id = :id")
    Optional<Cuenta> findByIdWithLock(@Param("id") Long id);
    
    @Query("SELECT c FROM Cuenta c WHERE c.id = :id")
    @QueryHints({@QueryHint(name = "jakarta.persistence.fetchgraph", value = "cuenta")})
    Optional<Cuenta> findByIdWithFetch(@Param("id") Long id);
}
