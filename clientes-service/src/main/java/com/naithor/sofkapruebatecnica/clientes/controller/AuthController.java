package com.naithor.sofkapruebatecnica.clientes.controller;

/*
 * =============================================================
 * IMPLEMENTACIÓN FUTURA - NO INCLUIDA EN ENTREGABLE
 * =============================================================
 * 
 * TODO (FUTURO): Agregar autenticación JWT para proteger endpoints
 * 
 * Beneficios esperados:
 * - Seguridad de endpoints
 * - Control de acceso por roles
 * - Tokens con expiración
 * 
 * Para habilitar:
 * 1. Agregar dependencias en pom.xml:
 *    - spring-boot-starter-security
 *    - io.jsonwebtoken:jjwt-api, jjwt-impl, jjwt-jackson
 * 2. Configurar JwtService y JwtAuthenticationFilter
 * 3. Habilitar SecurityConfig con protección de endpoints
 * 4. Descomentar este archivo
 * 
 * Endpoints de autenticación:
 * - POST /api/v1/auth/login - Login con usuario/contraseña
 * - POST /api/v1/auth/register - Registro con auto-login
 * 
 * Respuesta:
 * - { "token": "eyJ...", "type": "Bearer", "expiresIn": 86400 }
 * 
 * Headers requeridos en requests protegidos:
 * - Authorization: Bearer <token>
 */

// import com.naithor.sofkapruebatecnica.clientes.entity.Cliente;
// import com.naithor.sofkapruebatecnica.clientes.repository.ClienteRepository;
// import com.naithor.sofkapruebatecnica.shared.dto.AuthRequest;
// import com.naithor.sofkapruebatecnica.shared.dto.AuthResponse;
// import com.naithor.sofkapruebatecnica.shared.exception.AuthenticationException;
// import com.naithor.sofkapruebatecnica.shared.security.JwtService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// 
// @RestController
// @RequestMapping("/api/v1/auth")
// @RequiredArgsConstructor
// @Slf4j
// @Tag(name = "Autenticación", description = "Endpoints para autenticación JWT")
// public class AuthController {
//
//     private final JwtService jwtService;
//     private final ClienteRepository clienteRepository;
//     private final PasswordEncoder passwordEncoder;
//
//     @Value("${jwt.expiration}")
//     private long jwtExpiration;
//
//     @PostMapping("/login")
//     @Operation(summary = "Iniciar sesión", description = "Genera un token JWT para un cliente existente")
//     public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
//         log.debug("Login attempt for user: {}", request.username());
//         
//         Cliente cliente = clienteRepository.findByClienteId(request.username())
//                 .orElseThrow(() -> new AuthenticationException("AUTH_001", "Credenciales inválidas"));
//
//         if (!passwordEncoder.matches(request.password(), cliente.getContrasena())) {
//             throw new AuthenticationException("AUTH_001", "Credenciales inválidas");
//         }
//
//         String token = jwtService.generateToken(cliente.getClienteId());
//         log.info("User logged in successfully: {}", request.username());
//         return ResponseEntity.ok(AuthResponse.of(token, jwtExpiration));
//     }
//
//     @PostMapping("/register")
//     @Operation(summary = "Registrar cliente", description = "Registra un nuevo cliente y devuelve un token JWT")
//     public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest request) {
//         log.debug("Registration attempt for user: {}", request.username());
//         
//         if (clienteRepository.findByClienteId(request.username()).isPresent()) {
//             throw new AuthenticationException("AUTH_002", "El usuario ya existe");
//         }
//
//         Cliente cliente = new Cliente();
//         cliente.setClienteId(request.username());
//         cliente.setContrasena(passwordEncoder.encode(request.password()));
//         cliente.setNombre(request.username());
//         cliente.setEstado(true);
//         clienteRepository.save(cliente);
//
//         String token = jwtService.generateToken(request.username());
//         log.info("User registered successfully: {}", request.username());
//         return ResponseEntity.ok(AuthResponse.of(token, jwtExpiration));
//     }
// }

// Esta clase está deshabilitada - ver comentario arriba
// Por ahora la autenticación es abierta (sin JWT)
public class AuthController {
    // Implementación deshabilitada - ver comentario arriba
}
