package com.sistema.sistema_riego_inteligente.controladores;

import com.sistema.sistema_riego_inteligente.Entidad.Lectura;
import com.sistema.sistema_riego_inteligente.Entidad.Usuario;
import com.sistema.sistema_riego_inteligente.Repositorios.UsuarioRepositorio;
import com.sistema.sistema_riego_inteligente.servicios.ILecturaServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class LecturaControlador {

    @Autowired
    private ILecturaServicio lecturaServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/lecturas/usuario/{usuarioId}")
    public ResponseEntity<Page<Lectura>> obtenerPorUsuario(@PathVariable Long usuarioId, Pageable pageable) {
        return ResponseEntity.ok(lecturaServicio.obtenerPorUsuarioId(usuarioId, pageable));
    }

    @GetMapping("/lecturas/estado/{usuarioId}")
    public ResponseEntity<?> obtenerUltimoEstadoPorUsuario(@PathVariable Long usuarioId) {
        Lectura lectura = lecturaServicio.obtenerUltimoEstadoPorUsuario(usuarioId);
        if (lectura == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "No hay lecturas para este usuario."));
        }
        return ResponseEntity.ok(lectura);
    }

    @PostMapping("/guardar/{usuarioId}")
    public ResponseEntity<?> guardarLectura(@PathVariable Long usuarioId, @Valid @RequestBody Lectura lectura) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", "Usuario no encontrado."));
        }
        lectura.setId(null);
        lectura.setFechaRegistro(null);
        lectura.setUsuario(usuarioOpt.get());
        return new ResponseEntity<>(lecturaServicio.guardar(lectura), HttpStatus.CREATED);
    }

    @DeleteMapping("/lecturas/{id}")
    public ResponseEntity<?> eliminarLectura(@PathVariable Long id) {
        lecturaServicio.eliminar(id);
        return ResponseEntity.ok(Map.of("eliminado", Boolean.TRUE));
    }

    @PostMapping("/bomba/manual/encender")
    public ResponseEntity<?> activarManual() {
        lecturaServicio.activarManual();
        return ResponseEntity.ok(Map.of("bombaManual", true, "mensaje", "Bomba activada manualmente"));
    }

    @PostMapping("/bomba/manual/apagar")
    public ResponseEntity<?> desactivarManual() {
        lecturaServicio.desactivarManual();
        return ResponseEntity.ok(Map.of("bombaManual", false, "mensaje", "Bomba apagada manualmente"));
    }

    @GetMapping("/bomba/manual")
    public ResponseEntity<?> obtenerEstadoManual() {
        return ResponseEntity.ok(Map.of("bombaManual", lecturaServicio.estaActivaManual()));
    }
}