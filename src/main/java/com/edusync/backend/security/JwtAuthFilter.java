package com.edusync.backend.security;

import com.edusync.backend.repository.UsuarioRepository;
import com.edusync.backend.model.Usuario;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro de autenticación personalizado que intercepta cada petición HTTP (OncePerRequestFilter).
 * Extrae el token JWT de la cabecera 'Authorization', valida su firma y caducidad,
 * y establece la autenticación del usuario en el contexto de Spring Security si el token es válido.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Intercepta la petición para validar la presencia de un token Bearer en el encabezado.
     * Si el token es válido y el usuario existe en la base de datos, lo autentica dentro del contexto.
     *
     * @param request     Objeto con la información de la petición HTTP.
     * @param response    Objeto con la información de la respuesta HTTP.
     * @param filterChain Cadena de filtros a la que derivar el procesamiento.
     * @throws ServletException En caso de errores en la interceptación o servlet.
     * @throws IOException      En caso de errores de lectura/escritura en flujo de datos de red.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza por "Bearer ", continúa sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // quitar "Bearer "

        if (jwtUtils.validateToken(token)) {
            String email = jwtUtils.getEmailFromToken(token);

            // Cargamos el usuario para confirmar que existe en BD
            Usuario usuario = usuarioRepository.findByEmail(email);

            if (usuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
                        );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
