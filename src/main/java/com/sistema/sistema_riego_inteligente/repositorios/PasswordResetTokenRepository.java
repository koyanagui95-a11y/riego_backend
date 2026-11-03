package com.sistema.sistema_riego_inteligente.Repositorios;

import com.sistema.sistema_riego_inteligente.Entidad.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUsuarioId(Long usuarioId);
}