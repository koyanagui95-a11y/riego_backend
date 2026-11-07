package com.sistema.sistema_riego_inteligente.controladores;

import com.sistema.sistema_riego_inteligente.Entidad.ConfiguracionSensor;
import com.sistema.sistema_riego_inteligente.Repositorios.ConfiguracionSensorRepository;
import com.sistema.sistema_riego_inteligente.Repositorios.UsuarioRepositorio;
import com.sistema.sistema_riego_inteligente.dto.ConfiguracionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ConfiguracionControlador {

    @Autowired private ConfiguracionSensorRepository configRepo;
    @Autowired private UsuarioRepositorio usuarioRepositorio;

    @Value("${app.admin.key}")
    private String adminKey;

    @GetMapping("/config/{usuarioId}")
    public ResponseEntity<?> obtenerConfig(@PathVariable Long usuarioId) {
        if (usuarioRepositorio.findById(usuarioId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Usuario no encontrado"));
        }

        List<ConfiguracionSensor> lista = configRepo.findByUsuarioId(usuarioId);
        Map<String, Boolean> map = new HashMap<>();
        map.put("suelo", false);
        map.put("dht", false);
        map.put("caudal", false);
        map.put("tanque", false);
        map.put("bomba", false);

        for (ConfiguracionSensor c : lista) {
            map.put(c.getTipoSensor(), c.getHabilitado());
        }

        return ResponseEntity.ok(new ConfiguracionResponse(map));
    }

    @PostMapping("/admin/config/{usuarioId}")
    public ResponseEntity<?> actualizarConfig(
            @RequestHeader(value = "X-Admin-Key", required = false) String key,
            @PathVariable Long usuarioId,
            @RequestBody Map<String, Boolean> cambios) {

        if (key == null || !key.equals(adminKey)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No autorizado"));
        }
        if (usuarioRepositorio.findById(usuarioId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Usuario no encontrado"));
        }

        for (Map.Entry<String, Boolean> entry : cambios.entrySet()) {
            String tipo = entry.getKey();
            Boolean habilitado = entry.getValue();

            ConfiguracionSensor c = configRepo
                    .findByUsuarioIdAndTipoSensor(usuarioId, tipo)
                    .orElseGet(() -> {
                        ConfiguracionSensor nuevo = new ConfiguracionSensor();
                        nuevo.setUsuario(usuarioRepositorio.findById(usuarioId).get());
                        nuevo.setTipoSensor(tipo);
                        return nuevo;
                    });

            c.setHabilitado(habilitado);
            configRepo.save(c);
        }

        return ResponseEntity.ok(Map.of("mensaje", "Configuración actualizada"));
    }
}