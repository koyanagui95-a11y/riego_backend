package com.sistema.sistema_riego_inteligente.controladores;

import com.sistema.sistema_riego_inteligente.Entidad.ConfiguracionSensor;
import com.sistema.sistema_riego_inteligente.Repositorios.ConfiguracionSensorRepository;
import com.sistema.sistema_riego_inteligente.Repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        lista.sort((a, b) -> {
            int oa = a.getOrden() != null ? a.getOrden() : 999;
            int ob = b.getOrden() != null ? b.getOrden() : 999;
            return Integer.compare(oa, ob);
        });

        List<Map<String, Object>> sensores = new ArrayList<>();
        for (ConfiguracionSensor c : lista) {
            Map<String, Object> s = new HashMap<>();
            s.put("tipo", c.getTipoSensor());
            s.put("habilitado", c.getHabilitado());
            s.put("label", c.getLabel() != null ? c.getLabel() : c.getTipoSensor());
            s.put("unidad", c.getUnidad() != null ? c.getUnidad() : "");
            s.put("color", c.getColor() != null ? c.getColor() : "secondary");
            s.put("campo", c.getCampo() != null ? c.getCampo() : c.getTipoSensor());
            s.put("tipoValor", c.getTipoValor() != null ? c.getTipoValor() : "numero");
            s.put("valores", c.getValores() != null ? c.getValores() : new HashMap<>());
            s.put("orden", c.getOrden() != null ? c.getOrden() : 999);
            sensores.add(s);
        }

        return ResponseEntity.ok(Map.of("sensores", sensores));
    }

    @PostMapping("/sensores/registrar/{usuarioId}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> registrarSensores(
            @PathVariable Long usuarioId,
            @RequestBody List<Map<String, Object>> sensores) {

        if (usuarioRepositorio.findById(usuarioId).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Usuario no encontrado"));
        }

        int creados = 0;
        int actualizados = 0;

        for (Map<String, Object> s : sensores) {
            String tipo = (String) s.get("tipo");
            if (tipo == null) continue;

            ConfiguracionSensor c = configRepo
                    .findByUsuarioIdAndTipoSensor(usuarioId, tipo)
                    .orElseGet(() -> {
                        ConfiguracionSensor nuevo = new ConfiguracionSensor();
                        nuevo.setUsuario(usuarioRepositorio.findById(usuarioId).get());
                        nuevo.setTipoSensor(tipo);
                        nuevo.setHabilitado(true);
                        return nuevo;
                    });

            boolean esNuevo = c.getId() == null;

            if (s.containsKey("label"))      c.setLabel((String) s.get("label"));
            if (s.containsKey("unidad"))     c.setUnidad((String) s.get("unidad"));
            if (s.containsKey("color"))      c.setColor((String) s.get("color"));
            if (s.containsKey("campo"))      c.setCampo((String) s.get("campo"));
            if (s.containsKey("tipoValor"))  c.setTipoValor((String) s.get("tipoValor"));
            if (s.containsKey("valores"))    c.setValores((Map<String, Object>) s.get("valores"));
            if (s.containsKey("orden"))      c.setOrden(((Number) s.get("orden")).intValue());
            if (s.containsKey("habilitado")) c.setHabilitado((Boolean) s.get("habilitado"));

            configRepo.save(c);
            if (esNuevo) creados++;
            else actualizados++;
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("creados", creados);
        resp.put("actualizados", actualizados);
        return ResponseEntity.ok(resp);
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
            ConfiguracionSensor c = configRepo
                    .findByUsuarioIdAndTipoSensor(usuarioId, entry.getKey())
                    .orElseGet(() -> {
                        ConfiguracionSensor nuevo = new ConfiguracionSensor();
                        nuevo.setUsuario(usuarioRepositorio.findById(usuarioId).get());
                        nuevo.setTipoSensor(entry.getKey());
                        return nuevo;
                    });
            c.setHabilitado(entry.getValue());
            configRepo.save(c);
        }

        return ResponseEntity.ok(Map.of("mensaje", "Configuración actualizada"));
    }
} 