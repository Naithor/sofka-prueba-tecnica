package com.naithor.sofkapruebatecnica.cuentas.event;

import com.naithor.sofkapruebatecnica.shared.event.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento de dominio cuando se elimina un cliente (listener)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEliminadoEvent extends DomainEvent {
    private Long clienteId;
    private String identificacion;
}

