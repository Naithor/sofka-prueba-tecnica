package com.naithor.sofkapruebatecnica.gateway.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class GatewayRouter {

    private static final Logger log = LoggerFactory.getLogger(GatewayRouter.class);

    private final WebClient webClient;
    private final String clientesServiceUri;
    private final String cuentasServiceUri;

    public GatewayRouter(
            WebClient.Builder webClientBuilder,
            @Value("${gateway.servicios.clientes.uri}") String clientesServiceUri,
            @Value("${gateway.servicios.cuentas.uri}") String cuentasServiceUri) {
        this.webClient = webClientBuilder.build();
        this.clientesServiceUri = clientesServiceUri;
        this.cuentasServiceUri = cuentasServiceUri;
        log.info("Gateway initialized with clientes-service: {}", clientesServiceUri);
        log.info("Gateway initialized with cuentas-service: {}", cuentasServiceUri);
    }

    public RouterFunction<ServerResponse> routes() {
        return route(GET("/api/clientes/**"), this::proxyToClientesService)
                .andRoute(POST("/api/clientes/**"), this::proxyToClientesService)
                .andRoute(PUT("/api/clientes/**"), this::proxyToClientesService)
                .andRoute(DELETE("/api/clientes/**"), this::proxyToClientesService)
                .andRoute(GET("/api/cuentas/**"), this::proxyToCuentasService)
                .andRoute(POST("/api/cuentas/**"), this::proxyToCuentasService)
                .andRoute(PUT("/api/cuentas/**"), this::proxyToCuentasService)
                .andRoute(DELETE("/api/cuentas/**"), this::proxyToCuentasService)
                .andRoute(GET("/api/movimientos/**"), this::proxyToCuentasService)
                .andRoute(POST("/api/movimientos/**"), this::proxyToCuentasService)
                .andRoute(GET("/api/reportes/**"), this::proxyToCuentasService)
                .andRoute(GET("/actuator/health"), this::healthCheck);
    }

    private Mono<ServerResponse> proxyToClientesService(ServerRequest request) {
        return proxyRequest(request, clientesServiceUri, request.path());
    }

    private Mono<ServerResponse> proxyToCuentasService(ServerRequest request) {
        return proxyRequest(request, cuentasServiceUri, request.path());
    }

    private Mono<ServerResponse> proxyRequest(ServerRequest request, String targetHost, String newPath) {
        String queryString = request.uri().getQuery();
        String fullPath = queryString != null ? newPath + "?" + queryString : newPath;

        String correlationIdHeader = request.headers().asHttpHeaders().getFirst("X-Correlation-ID");
        final String correlationId = correlationIdHeader != null ? correlationIdHeader : java.util.UUID.randomUUID().toString();

        log.debug("Routing request: {} {} to {}{}", request.method(), newPath, targetHost, fullPath);

        final String finalCorrelationId = correlationId;
        Mono<ServerResponse> responseMono = webClient.method(request.method())
                .uri(URI.create(targetHost + fullPath))
                .headers(headers -> {
                    headers.addAll(request.headers().asHttpHeaders());
                    headers.set("X-Correlation-ID", finalCorrelationId);
                })
                .exchangeToMono(response -> handleResponse(response, finalCorrelationId));

        return responseMono.onErrorResume(e -> {
            log.error("Error routing request to {}: {}", targetHost, e.getMessage());
            return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{\"error\": \"Service unavailable\", \"correlationId\": \"" + finalCorrelationId + "\"}");
        });
    }

    private Mono<ServerResponse> handleResponse(ClientResponse response, String correlationId) {
        org.springframework.http.HttpStatusCode httpStatus = response.statusCode();
        MediaType contentType = response.headers().contentType().orElse(MediaType.APPLICATION_JSON);

        return response.bodyToMono(byte[].class)
                .flatMap(body -> ServerResponse.status(httpStatus)
                        .contentType(contentType)
                        .headers(headers -> {
                            headers.addAll(response.headers().asHttpHeaders());
                            headers.set("X-Correlation-ID", correlationId);
                        })
                        .bodyValue(body))
                .switchIfEmpty(Mono.defer(() ->
                        ServerResponse.status(httpStatus)
                                .contentType(contentType)
                                .headers(headers -> {
                                    headers.addAll(response.headers().asHttpHeaders());
                                    headers.set("X-Correlation-ID", correlationId);
                                })
                                .build()));
    }

    private Mono<ServerResponse> healthCheck(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"status\": \"UP\", \"service\": \"gateway-service\"}");
    }
}
