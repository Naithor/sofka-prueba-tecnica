package com.naithor.sofkapruebatecnica.shared.event;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DomainEvent implements Serializable {
    private String eventId;
    private long timestamp = System.currentTimeMillis();
}
