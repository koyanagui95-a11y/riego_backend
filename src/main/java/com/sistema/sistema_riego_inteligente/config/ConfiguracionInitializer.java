package com.sistema.sistema_riego_inteligente.config;

import com.sistema.sistema_riego_inteligente.Entidad.ConfiguracionSensor;
import com.sistema.sistema_riego_inteligente.Entidad.Usuario;
import com.sistema.sistema_riego_inteligente.Repositorios.ConfiguracionSensorRepository;
import com.sistema.sistema_riego_inteligente.Repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConfiguracionInitializer implements CommandLineRunner {

    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private ConfiguracionSensorRepository configRepo;

    @Override
    public void run(String... args) {
        List<Usuario> usuarios = usuarioRepositorio.findAll();
        int creadas = 0;

        for (Usuario u : usuarios) {
            creadas += asegurarSensor(u, "suelo", "Humedad del Suelo", "%", "success",
                    "humedad_suelo", "numero", null, 1, true);

            creadas += asegurarSensor(u, "dht", "Humedad Ambiente", "%", "info",
                    "humedad_ambiente", "numero", null, 2, true);

            creadas += asegurarSensor(u, "temperatura", "Temperatura", "°C", "danger",
                    "temperatura", "numero", null, 3, true);

            creadas += asegurarSensor(u, "caudal", "Flujo de Agua", "L/min", "primary",
                    "flujoAgua", "numero", null, 4, true);

            Map<String, Object> valsTanque = new HashMap<>();
            valsTanque.put("1", "LLENO");
            valsTanque.put("0", "BAJO");
            creadas += asegurarSensor(u, "tanque", "Nivel del Tanque", "", "primary",
                    "extras.tanque", "booleano", valsTanque, 5, false);

            creadas += asegurarSensor(u, "bomba", "Bomba", "", "secondary",
                    "bombaActiva", "booleano", null, 99, true);
        }

        System.out.println("[ConfigInit] Nuevas filas creadas: " + creadas);
    }

    private int asegurarSensor(Usuario u, String tipo, String label, String unidad,
                               String color, String campo, String tipoValor,
                               Map<String, Object> valores, int orden, boolean habilitado) {
        if (configRepo.findByUsuarioIdAndTipoSensor(u.getId(), tipo).isPresent()) return 0;

        ConfiguracionSensor c = new ConfiguracionSensor();
        c.setUsuario(u);
        c.setTipoSensor(tipo);
        c.setLabel(label);
        c.setUnidad(unidad);
        c.setColor(color);
        c.setCampo(campo);
        c.setTipoValor(tipoValor);
        c.setValores(valores != null ? valores : new HashMap<>());
        c.setOrden(orden);
        c.setHabilitado(habilitado);
        configRepo.save(c);
        return 1;
    }
}