package com.sistema.sistema_riego_inteligente.config;

import com.sistema.sistema_riego_inteligente.servicios.ILecturaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupReset implements CommandLineRunner {

    @Autowired
    private ILecturaServicio lecturaServicio;

    @Override
    public void run(String... args) {
        lecturaServicio.desactivarManual();
        System.out.println("[StartupReset] bombaManual reseteado a false al arrancar.");
    }
}