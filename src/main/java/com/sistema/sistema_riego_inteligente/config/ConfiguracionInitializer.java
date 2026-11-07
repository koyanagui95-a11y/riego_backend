package com.sistema.sistema_riego_inteligente.config;

import com.sistema.sistema_riego_inteligente.Entidad.ConfiguracionSensor;
import com.sistema.sistema_riego_inteligente.Entidad.Usuario;
import com.sistema.sistema_riego_inteligente.Repositorios.ConfiguracionSensorRepository;
import com.sistema.sistema_riego_inteligente.Repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfiguracionInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ConfiguracionSensorRepository configRepo;

    private static final String[] SENSORES_DEFAULT = {"suelo", "dht", "caudal", "tanque", "bomba"};

    @Override
    public void run(String... args) {
        List<Usuario> usuarios = usuarioRepositorio.findAll();
        int creadas = 0;
        int existentes = 0;

        for (Usuario u : usuarios) {
            for (String tipo : SENSORES_DEFAULT) {
                if (configRepo.findByUsuarioIdAndTipoSensor(u.getId(), tipo).isEmpty()) {
                    ConfiguracionSensor c = new ConfiguracionSensor();
                    c.setUsuario(u);
                    c.setTipoSensor(tipo);
                    c.setHabilitado(!tipo.equals("tanque"));
                    configRepo.save(c);
                    creadas++;
                } else {
                    existentes++;
                }
            }
        }

        System.out.println("[ConfigInit] Configuraciones creadas: " + creadas);
        System.out.println("[ConfigInit] Configuraciones existentes: " + existentes);
    }
}