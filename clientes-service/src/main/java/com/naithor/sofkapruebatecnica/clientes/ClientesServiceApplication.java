package com.naithor.sofkapruebatecnica.clientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Aplicación principal del servicio de clientes
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.naithor.sofkapruebatecnica.clientes",
    "com.naithor.sofkapruebatecnica.shared"
})
public class ClientesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientesServiceApplication.class, args);
    }

}

