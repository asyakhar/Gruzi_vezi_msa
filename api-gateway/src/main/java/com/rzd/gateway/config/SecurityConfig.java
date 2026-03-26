package com.rzd.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            // Отключаем CSRF, так как мы используем JWT-токены
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            // Подключаем ваши настройки CORS из CorsConfig
            .cors(cors -> {}) 
            // Разрешаем Gateway пропускать все запросы дальше (авторизацию проверяет ваш JwtAuthenticationFilter)
            .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll());
            
        return http.build();
    }
}