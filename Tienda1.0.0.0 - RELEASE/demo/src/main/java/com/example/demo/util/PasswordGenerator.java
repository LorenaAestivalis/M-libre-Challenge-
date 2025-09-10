package com.example.demo.util;

/*
 * 
 * 
 * SE DEBE ELIMINAR PARA DESPLIEGUE A PRODUCCION - NO ES FUNDAMENTAL PARA EL PROCESO
 * OBJETIVO: CIFRAR LAS CONTRASEÑAS QUE SE ALMACENARAN EN EL CSV (BD SIMULADA) 
 * CON LOS USUARIOS QUE SE CONECTARAN A OBTENER SU TOKEN
 * 
 */
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Cambia estas contraseñas por las que quieras generar
        String rawPassword1 = "M3rc4d0*L1br3%2025";
        String rawPassword2 = "M3rc4d0*L1br3%2025-Lorena";

        String encodedPassword1 = encoder.encode(rawPassword1);
        String encodedPassword2 = encoder.encode(rawPassword2);

        System.out.println("Password 1234 => " + encodedPassword1);
        System.out.println("Password secreta123 => " + encodedPassword2);
    }
}
