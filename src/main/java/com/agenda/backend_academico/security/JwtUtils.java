package com.agenda.backend_academico.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Clave secreta de al menos 256 bits para HS256. Cámbiala en producción
    // y externalízala en application.properties o una variable de entorno.
    private static final String SECRET =
            "AgendaAcademicaSecretKeyParaJWT_DebeSerLarga2025!"; // >= 32 chars

    private static final long EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000; // 7 días

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    /** Genera un token JWT firmado con el email del usuario como 'subject'. */
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

    /** Devuelve el email (subject) embebido en el token. */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** Devuelve true si el token tiene firma válida y no ha expirado. */
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
