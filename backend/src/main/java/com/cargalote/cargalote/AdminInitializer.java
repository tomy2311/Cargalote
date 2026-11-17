
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

            if (!repository.existsByUsername("supervisor")) {

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                Usuario supervisor = new Usuario(
                        "Supervisor",
                        "supervisor@cargalote.com",
                        "supervisor",
                        encoder.encode("thomas"),
                        "Administrador"
                );

                repository.save(supervisor);

                System.out.println("Administrador creado correctamente.");

            } else {
                System.out.println("El administrador ya existe.");
            }
        };
    }
}
