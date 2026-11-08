package com.sistema.sistema_riego_inteligente.config;

import com.sistema.sistema_riego_inteligente.Entidad.Usuario;
import com.sistema.sistema_riego_inteligente.Repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String username = "admin.riego";
        String email = "admin.riego@gmail.com";
        String passwordTemporal = "Admin1234";

        if (usuarioRepositorio.findByUsername(username).isPresent()) {
            System.out.println("[DataSeeder] El usuario " + username + " ya existe. No se crea de nuevo.");
            return;
        }

        Usuario admin = new Usuario();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(passwordTemporal));
        usuarioRepositorio.save(admin);

        System.out.println("[DataSeeder] Usuario creado: " + username + " / " + passwordTemporal);
    }
}