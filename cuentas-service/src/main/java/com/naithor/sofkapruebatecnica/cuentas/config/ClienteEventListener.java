package com.naithor.sofkapruebatecnica.cuentas.config;

import com.naithor.sofkapruebatecnica.cuentas.event.ClienteEliminadoEvent;
import com.naithor.sofkapruebatecnica.cuentas.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Listener para eventos de clientes
 */
@Service
@RequiredArgsConstructor
public class ClienteEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(ClienteEventListener.class);
    
    private final CuentaRepository cuentaRepository;
    
    /**
     * Escuchar evento cuando se elimina un cliente
     */
    @RabbitListener(queues = RabbitMQConfig.CLIENTE_ELIMINADO_QUEUE)
    public void handleClienteEliminado(ClienteEliminadoEvent evento) {
        log.info("Recibido evento ClienteEliminado para cliente ID: {}", evento.getClienteId());
        
        // Desactivar todas las cuentas del cliente eliminado
        var cuentas = cuentaRepository.findByClienteId(evento.getClienteId());
        cuentas.forEach(cuenta -> {
            cuenta.setEstado(false);
            cuentaRepository.save(cuenta);
            log.info("Cuenta {} desactivada por eliminación de cliente", cuenta.getNumeroCuenta());
        });
    }
}

