package com.example.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * CsvUserDetailsService
 *
 * Implementación que carga los usuarios
 * desde un archivo CSV almacenado en `resources/BD_USERS.csv`.
 *
 * Formato esperado del CSV (sin cabecera):
 * username,passwordEncriptado,ROLE_XYZ
 *
 * Ejemplo:
 * admin,$2a$10$abc123...xyz,ROLE_ADMIN
 * user,$2a$10$qwe456...mno,ROLE_USER
 */
@Service
public class CsvUserDetailsService implements UserDetailsService {

    // Cache en memoria para los usuarios cargados desde el CSV
    private final Map<String, UserDetails> users = new HashMap<>();

    /**
     * Constructor: lee el archivo CSV al iniciar la aplicación
     * y carga los usuarios en memoria.
     */
    public CsvUserDetailsService(PasswordEncoder passwordEncoder) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/TABLE_USERS.csv")))) {
            // Saltamos la primera línea (cabecera)
            br.lines().skip(1).forEach(line -> {
                String[] parts = line.split(",");
                String username = parts[0].trim();
                String password = parts[1].trim(); // ya encriptada con BCrypt
                String role = parts[2].trim();
                // Creamos un UserDetails y lo almacenamos en el mapa
                users.put(username, User.withUsername(username)
                        .password(password)
                        .roles(role.replace("ROLE_", "")) // Spring ya antepone ROLE_
                        .build());
            });

        } catch (Exception e) {
            throw new RuntimeException("Error al cargar usuarios desde BD_USERS.csv", e);
        }
    }

    // Busca usuario
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        return user;
    }
}
