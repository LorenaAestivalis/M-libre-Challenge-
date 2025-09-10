package com.example.demo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.demo.filter.JwtFilter;
import com.example.demo.service.CsvUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita @PreAuthorize
/*
 * sirve para restringir el acceso a métodos (o endpoints REST) en
 * función de una expresión de seguridad que se evalúa antes de ejecutar
 * el método.
 */

public class SecurityConfig {
    private static final long HSTS_MAX_AGE = 31536000;
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * PasswordEncoder seguro (BCrypt)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * UserDetailsService basado en CSV que simula la base de datos con usuarios,
     * passords y roles de los que consumiran el web service
     */
    @Bean
    public UserDetailsService userDetailsService(CsvUserDetailsService csvUserDetailsService) {
        return csvUserDetailsService;
    }

    /**
     * Configuración de seguridad HTTP
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                /**
                 * Explicación:
                 * - Por defecto, Spring Security crea y mantiene una sesión (HttpSession)
                 * para guardar el estado del usuario autenticado.
                 * - En un esquema con JWT no queremos eso, porque:
                 * Cada request trae su propio token en el header Authorization.
                 * El servidor NO guarda información de sesión (es más escalable).
                 * Cualquier instancia del backend puede validar el token sin compartir estado.
                 *
                 * - Con `STATELESS`:
                 * No se crean ni usan sesiones Http.
                 * Cada petición debe traer el JWT válido.
                 * Si el token expira o es inválido → 401 Unauthorized.
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Cabeceras de seguridad
                .headers(headers -> headers
                        // Content Security Policy (CSP)
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                        /**
                         * Explicación:
                         * - CSP ayuda a prevenir ataques de inyección de contenido (ej: XSS).
                         * - "default-src 'self'" indica que SOLO se permiten recursos cargados
                         * desde el mismo dominio del backend (bloquea scripts externos maliciosos).
                         * - Se puede ampliar con img-src, script-src, etc. según el frontend.
                         */
                        // HTTP Strict Transport Security (HSTS)
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true) // Aplica también a subdominios
                                .maxAgeInSeconds(HSTS_MAX_AGE)) // Fuerza HTTPS durante 1 año - el navegador recordará
                                                                // durante 1 anno que debe usar siempre HTTPS con tu
                                                                // dominio.
                        /**
                         * Explicación:
                         * - Obliga a que los navegadores usen SIEMPRE HTTPS al conectar al dominio.
                         * - Protege contra ataques "SSL stripping".
                         */
                        // Deshabilitar iframes (Clickjacking protection)
                        /**
                         * Explicación:
                         * - Impide que tu aplicación sea embebida dentro de un <iframe>.
                         * - Previene ataques de "clickjacking".
                         * - Si en algún caso necesitas iframes legítimos, se puede cambiar a
                         * sameOrigin().
                         */
                        .frameOptions(frame -> frame.deny()))

                // CORS (Cross-Origin Resource Sharing) para frontends permitidos
                .cors(cors -> cors.configurationSource(request -> {
                    // Permitir solo orígenes y métodos específicos
                    CorsConfiguration config = new CorsConfiguration();
                    // Orígenes permitidos (dominios que pueden hacer peticiones al backend)
                    config.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:4200"));
                    // Métodos HTTP permitidos
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // ampliado
                    // Headers que el cliente puede enviar
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    // Permitir cookies / credenciales (ej: Authorization header)
                    config.setAllowCredentials(true);
                    return config;
                }))

                // Forzar HTTPS en todas las peticiones
                .requiresChannel(channel -> channel.anyRequest().requiresSecure())

                // Autorización de endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/error").permitAll() // permitir login y errores sin autenticar
                        .requestMatchers("/productos/**").hasRole("ADMIN")
                        // .requestMatchers("/productos/cambiar-precio").hasRole("ADMIN") // solo el
                        // roll ADMIN puede
                        // acceder a
                        // /admin/**
                        .requestMatchers("/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html")
                        .permitAll()
                        .anyRequest().authenticated() // el resto de endpoints requieren autenticación
                )

                // Desactivar CSRF porque usamos JWT
                /*
                 * Cross-Site Request Forgery: un ataque donde un usuario autenticado
                 * en tu app es "engañado" para ejecutar acciones sin querer
                 * (ej: hacer POST desde un sitio malicioso aprovechando su sesión).
                 */
                .csrf(csrf -> csrf.disable())

                // Filtro JWT antes del de autenticación estándar
                /**
                 * Contexto:
                 * - Spring Security tiene una "cadena de filtros" que procesan cada request
                 * en un orden específico (autenticación, autorización, etc).
                 * - Por defecto, UsernamePasswordAuthenticationFilter se encarga de leer
                 * usuario/contraseña (form login o basic auth).
                 *
                 * ¿Qué hace JwtFilter?
                 * - Intercepta cada request.
                 * - Busca el header "Authorization: Bearer <token>".
                 * - Valida el JWT (firma, expiración).
                 * - Si es válido, crea un objeto Authentication con el usuario + roles.
                 * - Mete ese Authentication en el SecurityContext.
                 * 
                 */
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        /*
         * ¿Qué hace .build()?
         * - Compila todas esas reglas y devuelve un objeto SecurityFilterChain.
         * - Ese SecurityFilterChain es el que realmente Spring usa
         * para procesar las peticiones entrantes.
         */
        return http.build();
    }
}
