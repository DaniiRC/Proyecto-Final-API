package com.edusync.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utilidades para la gestión de tokens JSON Web Tokens (JWT).
 * Proporciona métodos para la generación, extracción de información y validación de tokens de seguridad.
 */
@Component
public class JwtUtils {

    // Clave secreta de al menos 256 bits para HS256. Cámbiala en producción
    // y externalízala en application.properties o una variable de entorno.
    private static final String SECRET =
            "AgendaAcademicaSecretKeyParaJWT_DebeSerLarga2025!"; // >= 32 chars

    private static final long EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000; // 7 días

    /**
     * Obtiene la clave de firma HMAC basada en la cadena secreta codificada.
     *
     * @return Objeto Key para firmar y validar tokens.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /**
     * Genera un token JWT firmado codificando el email del usuario como identificador (subject)
     * y configurando la fecha de emisión y de expiración.
     *
     * @param email Correo electrónico del usuario.
     * @return Cadena compacta que representa el token firmado.
     */
    public String generateToken(String email) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el correo electrónico (subject) contenido dentro de los claims del token JWT.
     *
     * @param token Cadena del token de seguridad.
     * @return El correo electrónico del usuario extraído.
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida de manera segura que la firma del token sea correcta y no haya expirado.
     *
     * @param token Cadena del token a validar.
     * @return true si el token es estructuralmente correcto y vigente; false de lo contrario.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
