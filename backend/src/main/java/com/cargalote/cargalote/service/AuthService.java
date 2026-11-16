
package com.cargalote.cargalote.service;

import com.cargalote.cargalote.entity.Usuario;
import com.cargalote.cargalote.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String register(String name, String email, String username, String password) {

        if (name.isBlank() || email.isBlank()
                || username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Completa todos los campos.");
        }

        if (usuarioRepository.existsByEmail(email)
                || usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "El usuario o correo ya está registrado."
            );
        }

        Usuario nuevoUsuario = new Usuario(
                name,
                email,
                username,
                encoder.encode(password),
                "Jefe de Turno"
        );

        usuarioRepository.save(nuevoUsuario);

        return "Cuenta creada correctamente. Ya puedes iniciar sesión.";
    }

    public User login(String username, String password) {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Credenciales incorrectas. Verifica usuario y contraseña."
                ));

        if (!encoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException(
                    "Credenciales incorrectas. Verifica usuario y contraseña."
            );
        }

        return new User(
                usuario.getUsername(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }

    public record User(
            String username,
            String name,
            String email,
            String role
    ) {
    }
}
