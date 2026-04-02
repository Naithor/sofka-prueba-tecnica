package com.naithor.sofkapruebatecnica.clientes.event;

import com.naithor.sofkapruebatecnica.shared.event.DomainEvent;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEliminadoEvent extends DomainEvent {
    private Long clienteId;
    private String identificacion;
}
