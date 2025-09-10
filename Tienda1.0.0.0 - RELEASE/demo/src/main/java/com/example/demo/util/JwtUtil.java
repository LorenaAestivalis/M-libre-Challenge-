package com.example.demo.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JwtUtil
 *
 * Clase de utilidad para generar, validar y extraer datos de tokens JWT
 * usando firma asimétrica RSA (RS256).
 *
 * - Genera tokens firmados con la clave privada (`jwtRS256.key`).
 * - Valida tokens usando la clave pública (`jwtRS256.key.pub`).
 * - Incluye roles como claim personalizado.
 */
@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 15 * 60 * 1000; // token vigente unicamente 15 minutos
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtUtil() throws Exception {
        this.privateKey = loadPrivateKey("/jwtRS256.key");
        this.publicKey = loadPublicKey("/jwtRS256.key.pub");
    }

    // Cargar clave privada en formato PEM
    private PrivateKey loadPrivateKey(String path) throws Exception {
        String key = readKeyFromFile(path)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    // 🔑 Cargar clave pública en formato PEM
    private PublicKey loadPublicKey(String path) throws Exception {
        String key = readKeyFromFile(path)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    // 📂 Leer archivo desde resources
    private String readKeyFromFile(String path) throws IOException {
        return new String(new ClassPathResource(path).getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    // Generar token con roles incluidos
    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // usuario
                .claim("roles", roles) // rol del usuario
                .setIssuedAt(new Date()) // fecha de emision del token
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // fecha de vencimiento del token
                .signWith(privateKey, SignatureAlgorithm.RS256) // firmado del token con clave privada
                .compact();
    }

    // Extraer username
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)// valida firma con clave pública
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extraer roles
    public List<String> extractRoles(String token) {
        return (List<String>) Jwts.parserBuilder()
                .setSigningKey(publicKey) // valida firma con clave pública
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);
    }

    // Validar token
    /** Valida que el token pertenezca al usuario y no esté expirado */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /** Comprueba si el token ya expiró */
    private boolean isTokenExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            // Esto se captura aquí si el token ya expiró ANTES de la validación principal.
            // La validación principal ya lo hace, pero si quieres loggear específicamente
            // aquí:
            logger.warn("Token expirado: {}", token);
            return true;
        } catch (Exception e) {
            // Manejar otras posibles excepciones de parsing si fuera necesario
            logger.error("Token Invalido", e);
            return true; // Asumir expirado o inválido si hay un error
        }
    }
}
