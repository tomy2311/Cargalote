package com.cargalote.cargalote.service;



import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public String register(String name, String email, String username, String password) {

        if (name.isBlank() || email.isBlank()
                || username.isBlank() || password.isBlank()) {

            throw new IllegalArgumentException("Completa todos los campos.");
        }

        boolean userExists = users.values().stream()
                .anyMatch(user ->
                        user.email().equalsIgnoreCase(email)
                                || user.username().equalsIgnoreCase(username)
                );

        if (userExists) {
            throw new IllegalArgumentException(
                    "El usuario o correo ya está registrado."
            );
        }

        User newUser = new User(
                username,
                name,
                email,
                password,
                "Jefe de Turno"
        );

        users.put(username.toLowerCase(), newUser);

        return "Cuenta creada correctamente. Ya puedes iniciar sesión.";
    }

    public User login(String username, String password) {

        User user = users.get(username.toLowerCase());

        if (user == null || !user.password().equals(password)) {
            throw new IllegalArgumentException(
                    "Credenciales incorrectas. Verifica usuario y contraseña."
            );
        }

        return user;
    }

    public record User(
            String username,
            String name,
            String email,
            String password,
            String role
    ) {
    } 
}