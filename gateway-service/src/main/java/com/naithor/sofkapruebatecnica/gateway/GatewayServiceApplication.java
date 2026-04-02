package com.naithor.sofkapruebatecnica.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Map;

@SpringBootApplication
@RestController
public class GatewayServiceApplication {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().responseTimeout(Duration.ofSeconds(10))));
    }

    @Bean
    public com.naithor.sofkapruebatecnica.gateway.router.GatewayRouter gatewayRouter(
            WebClient.Builder builder,
            org.springframework.core.env.Environment env) {
        String clientesUri = env.getProperty("gateway.servicios.clientes.uri", "http://localhost:8081");
        String cuentasUri = env.getProperty("gateway.servicios.cuentas.uri", "http://localhost:8082");
        return new com.naithor.sofkapruebatecnica.gateway.router.GatewayRouter(builder, clientesUri, cuentasUri);
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @RequestMapping("/actuator/health")
    public Mono<Map<String, String>> health() {
        return Mono.just(Map.of("status", "UP"));
    }
}
