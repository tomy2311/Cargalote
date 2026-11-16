
package com.cargalote.cargalote;

import com.cargalote.cargalote.entity.Usuario;
import com.cargalote.cargalote.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner crearAdministrador(UsuarioRepository repository) {
        return args -> {

            if (!repository.existsByUsername("admin")) {

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                Usuario admin = new Usuario(
                        "Administrador",
                        "admin@cargalote.com",
                        "admin",
                        encoder.encode("123456"),
                        "Administrador"
                );

                repository.save(admin);

                System.out.println("Administrador creado correctamente.");
            }
        };
    }
}
