package com.sistema.sistema_riego_inteligente.controladores;

import com.sistema.sistema_riego_inteligente.Entidad.Usuario;
import com.sistema.sistema_riego_inteligente.Repositorios.UsuarioRepositorio;
import com.sistema.sistema_riego_inteligente.dto.RegisterRequest;
import com.sistema.sistema_riego_inteligente.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminControlador {

    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${app.admin.key}")
    private String adminKey;

    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(
            @RequestHeader(value = "X-Admin-Key", required = false) String key,
            @Valid @RequestBody RegisterRequest request) {

        if (key == null || !key.equals(adminKey)) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No autorizado"));
        }
        if (usuarioRepositorio.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body(new UserResponse(null, "El usuario ya existe."));
        }
        if (usuarioRepositorio.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body(new UserResponse(null, "El correo ya está registrado."));
        }

        Usuario u = new Usuario();
        u.setUsername(request.username());
        u.setEmail(request.email());
        u.setPassword(passwordEncoder.encode(request.password()));
        Usuario guardado = usuarioRepositorio.save(u);
        return ResponseEntity.ok(new UserResponse(guardado.getId(), guardado.getUsername()));
    }
}