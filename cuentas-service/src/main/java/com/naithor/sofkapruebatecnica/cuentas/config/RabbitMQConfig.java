package com.naithor.sofkapruebatecnica.cuentas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el servicio de cuentas
 */
@Configuration
public class RabbitMQConfig {
    
    public static final String CLIENTES_EXCHANGE = "clientes-exchange";
    public static final String CLIENTE_ELIMINADO_QUEUE = "cuentas-cliente-eliminado-queue";
    public static final String CLIENTE_ELIMINADO_ROUTING_KEY = "cliente.eliminado";
    
    @Bean
    public DirectExchange clientesExchange() {
        return new DirectExchange(CLIENTES_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue clienteEliminadoQueue() {
        return new Queue(CLIENTE_ELIMINADO_QUEUE, true);
    }
    
    @Bean
    public Binding bindingClienteEliminado() {
        return BindingBuilder.bind(clienteEliminadoQueue())
            .to(clientesExchange())
            .with(CLIENTE_ELIMINADO_ROUTING_KEY);
    }
}

