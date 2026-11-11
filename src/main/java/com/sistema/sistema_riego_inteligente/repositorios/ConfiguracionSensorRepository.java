package com.sistema.sistema_riego_inteligente.Repositorios;

import com.sistema.sistema_riego_inteligente.Entidad.ConfiguracionSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionSensorRepository extends JpaRepository<ConfiguracionSensor, Long> {
    List<ConfiguracionSensor> findByUsuarioId(Long usuarioId);
    Optional<ConfiguracionSensor> findByUsuarioIdAndTipoSensor(Long usuarioId, String tipoSensor);
}