package com.study.messages.config;

import com.study.messages.exceptions.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, SecurityResponseWriter writer,
                                                 UrlBasedCorsConfigurationSource corsSource) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .requestCache(cache -> cache.requestCache(NoOpServerRequestCache.getInstance()))
                .cors(cors -> cors.configurationSource(corsSource))
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((exchange, e) -> writer.write(exchange, ErrorCode.UNAUTHORIZED))
                        .accessDeniedHandler((exchange, e) -> handleAccessDenied(exchange, writer)))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET, "/api/v1/messages/feed").hasRole("subscriber")
                        .pathMatchers(HttpMethod.GET, "/api/v1/messages", "/api/v1/messages/*").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/messages").hasRole("producer")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/messages/*").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/messages/*").authenticated()
                        .pathMatchers("/api/v1/subscriptions/**", "/api/v1/subscribers/**").hasRole("subscriber")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint((exchange, e) -> writer.write(exchange, ErrorCode.UNAUTHORIZED))
                        .accessDeniedHandler((exchange, e) -> handleAccessDenied(exchange, writer)))
                .build();
    }

    private Mono<Void> handleAccessDenied(ServerWebExchange exchange, SecurityResponseWriter writer) {
        var request = exchange.getRequest();
        ErrorCode code = HttpMethod.POST.equals(request.getMethod())
                && "/api/v1/messages".equals(request.getPath().value())
                ? ErrorCode.PRODUCER_ONLY
                : ErrorCode.FORBIDDEN;
        return writer.write(exchange, code);
    }

    private ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        var authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("ROLE_");
        var hierarchy = RoleHierarchyImpl.fromHierarchy("""
                ROLE_administrator > ROLE_producer
                ROLE_producer > ROLE_subscriber
                """);
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt ->
                hierarchy.getReachableGrantedAuthorities(authorities.convert(jwt)));
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
        configuration.setMaxAge(3600L);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
