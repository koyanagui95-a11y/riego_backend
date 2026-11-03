package com.sistema.sistema_riego_inteligente.controladores;

import com.sistema.sistema_riego_inteligente.Entidad.PasswordResetToken;
import com.sistema.sistema_riego_inteligente.Entidad.Usuario;
import com.sistema.sistema_riego_inteligente.Repositorios.PasswordResetTokenRepository;
import com.sistema.sistema_riego_inteligente.Repositorios.UsuarioRepositorio;
import com.sistema.sistema_riego_inteligente.dto.ChangePasswordRequest;
import com.sistema.sistema_riego_inteligente.dto.ForgotPasswordRequest;
import com.sistema.sistema_riego_inteligente.dto.LoginResquest;
import com.sistema.sistema_riego_inteligente.dto.ResetPasswordRequest;
import com.sistema.sistema_riego_inteligente.dto.UserResponse;
import com.sistema.sistema_riego_inteligente.servicios.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

    @Autowired private UsuarioRepositorio usuarioRepositorio;
    @Autowired private PasswordResetTokenRepository tokenRepositorio;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.reset.token.minutos:30}")
    private int minutosToken;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginResquest request) {
        Optional<Usuario> encontrado = usuarioRepositorio.findByUsername(request.username());
        if (encontrado.isEmpty()
                || !passwordEncoder.matches(request.password(), encontrado.get().getPassword())) {
            return ResponseEntity.status(401).body(new UserResponse(null, "Credenciales inválidas."));
        }
        Usuario u = encontrado.get();
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getUsername()));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> cambiarPassword(@Valid @RequestBody ChangePasswordRequest request) {
        Optional<Usuario> encontrado = usuarioRepositorio.findByUsername(request.username());
        if (encontrado.isEmpty()
                || !passwordEncoder.matches(request.currentPassword(), encontrado.get().getPassword())) {
            return ResponseEntity.status(401).body(new UserResponse(null, "Credenciales inválidas."));
        }
        Usuario u = encontrado.get();
        u.setPassword(passwordEncoder.encode(request.newPassword()));
        usuarioRepositorio.save(u);
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getUsername()));
    }

    @PostMapping("/forgot-password")
    @Transactional
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(request.email());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            tokenRepositorio.deleteByUsuarioId(usuario.getId());

            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            String token = HexFormat.of().formatHex(bytes);

            PasswordResetToken reset = new PasswordResetToken();
            reset.setToken(token);
            reset.setUsuario(usuario);
            reset.setExpiraEn(LocalDateTime.now().plusMinutes(minutosToken));
            tokenRepositorio.save(reset);

            String link = frontendUrl + "/reset-password.html?token=" + token;
            emailService.enviarResetPassword(usuario.getEmail(), usuario.getUsername(), link);
        }

        return ResponseEntity.ok(Map.of(
                "mensaje", "Si el correo está registrado, recibirás un enlace de recuperación."
        ));
    }

    @PostMapping("/reset-password")
    @Transactional
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        Optional<PasswordResetToken> opt = tokenRepositorio.findByToken(request.token());

        if (opt.isEmpty() || !opt.get().estaVigente()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "mensaje", "El enlace es inválido o ha expirado."
            ));
        }

        PasswordResetToken reset = opt.get();
        Usuario usuario = reset.getUsuario();
        usuario.setPassword(passwordEncoder.encode(request.newPassword()));
        usuarioRepositorio.save(usuario);

        reset.setUsado(true);
        tokenRepositorio.save(reset);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente."));
    }
}