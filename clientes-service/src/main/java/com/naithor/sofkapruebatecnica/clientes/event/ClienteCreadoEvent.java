package com.naithor.sofkapruebatecnica.clientes.event;

import com.naithor.sofkapruebatecnica.shared.event.DomainEvent;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCreadoEvent extends DomainEvent {
    private Long clienteId;
    private String nombre;
    private String identificacion;
}
