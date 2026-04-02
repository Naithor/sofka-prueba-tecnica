package com.naithor.sofkapruebatecnica.cuentas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Aplicación principal del servicio de cuentas
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.naithor.sofkapruebatecnica.cuentas",
    "com.naithor.sofkapruebatecnica.shared"
})
public class CuentasServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CuentasServiceApplication.class, args);
    }

}

