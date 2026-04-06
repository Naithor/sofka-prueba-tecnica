package com.naithor.sofkapruebatecnica.cuentas.config;

/*
 * =============================================================
 * IMPLEMENTACIÓN FUTURA - NO INCLUIDA EN ENTREGABLE
 * =============================================================
 * 
 * TODO (FUTURO): Habilitar Redis para caché de consultas frecuentes
 * 
 * Beneficios esperados:
 * - Reducir carga en base de datos
 * - Mejorar tiempos de respuesta para reportes
 * - Disminuir latencia en lecturas de saldos
 * 
 * Para habilitar descomentar y configurar Redis en docker-compose:
 * 1. Agregar servicio redis en docker-compose.yml
 * 2. Configurar spring.data.redis.* en application.yml
 * 3. Descomentar dependencias en pom.xml:
 *    - spring-boot-starter-data-redis
 *    - spring-boot-starter-cache
 */

// import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.cache.RedisCacheConfiguration;
// import org.springframework.data.redis.cache.RedisCacheManager;
// import org.springframework.data.redis.connection.RedisConnectionFactory;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
// import org.springframework.data.redis.serializer.RedisSerializationContext;
// import org.springframework.data.redis.serializer.StringRedisSerializer;
// 
// import java.time.Duration;
// import java.util.HashMap;
// import java.util.Map;
// 
// @Configuration
// @EnableCaching
// public class RedisConfig {
//
//     @Bean
//     public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//         RedisTemplate<String, Object> template = new RedisTemplate<>();
//         template.setConnectionFactory(connectionFactory);
//         template.setKeySerializer(new StringRedisSerializer());
//         template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//         template.setHashKeySerializer(new StringRedisSerializer());
//         template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//         template.afterPropertiesSet();
//         return template;
//     }
//
//     @Bean
//     public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//         RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
//                 .entryTtl(Duration.ofMinutes(10))
//                 .serializeKeysWith(RedisSerializationContext.SerializationPair
//                         .fromSerializer(new StringRedisSerializer()))
//                 .serializeValuesWith(RedisSerializationContext.SerializationPair
//                         .fromSerializer(new GenericJackson2JsonRedisSerializer()));
//
//         Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
//         cacheConfigurations.put("cuentas", defaultConfig.entryTtl(Duration.ofMinutes(5)));
//         cacheConfigurations.put("cuentaById", defaultConfig.entryTtl(Duration.ofMinutes(15)));
//         cacheConfigurations.put("allCuentas", defaultConfig.entryTtl(Duration.ofMinutes(1)));
//         cacheConfigurations.put("reportes", defaultConfig.entryTtl(Duration.ofMinutes(2)));
//
//         return RedisCacheManager.builder(connectionFactory)
//                 .cacheDefaults(defaultConfig)
//                 .withInitialCacheConfigurations(cacheConfigurations)
//                 .transactionAware()
//                 .build();
//     }
// }

public class RedisConfig {
    // Implementación deshabilitada - ver comentario arriba
}
