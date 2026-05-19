package com.edusync.backend.config;

import com.edusync.backend.security.JwtAuthFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración central de la seguridad de la aplicación (Spring Security).
 * Define políticas de estado, control de accesos por rutas (públicas y protegidas),
 * encriptación de contraseñas mediante BCrypt y la inyección del filtro JWT personalizado.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * Define el bean del encriptador de contraseñas BCryptPasswordEncoder.
     * Utilizado para codificar las claves de los usuarios de manera segura antes de guardarlas.
     *
     * @return Instancia del codificador de contraseñas.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP, estableciendo qué endpoints son públicos
     * y cuáles están protegidos tras tokens JWT, además de declarar el estado stateless.
     *
     * @param http Componente de configuración de seguridad de Spring.
     * @return Filtro configurado para el motor de Spring Security.
     * @throws Exception En caso de errores en la configuración interna de la cadena de filtros.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desactivar CSRF (API REST stateless, no lo necesita)
            .csrf(csrf -> csrf.disable())

            // Stateless: Spring Security no crea sesiones HTTP
            .sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas (sin token)
                .requestMatchers(
                    "/api/usuarios/login",
                    "/api/usuarios/registro",
                    "/api/usuarios/google-login",
                    "/api/usuarios/enviar-codigo",
                    "/api/usuarios/verificar-y-cambiar",
                    "/api/eventos/*/nota",
                    "/uploads/**"
                ).permitAll()
                // Todo lo demás requiere token válido
                .anyRequest().authenticated()
            )

            // Añadir nuestro filtro JWT antes del filtro estándar de usuario/contraseña
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
