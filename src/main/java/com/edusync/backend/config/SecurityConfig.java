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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /** Bean de BCrypt disponible en toda la aplicación. */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
