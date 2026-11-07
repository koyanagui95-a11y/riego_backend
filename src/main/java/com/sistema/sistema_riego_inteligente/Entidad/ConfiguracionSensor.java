package com.sistema.sistema_riego_inteligente.Entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion_sensores", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "tipo_sensor"})
})
public class ConfiguracionSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_sensor", nullable = false, length = 50)
    private String tipoSensor;

    @Column(nullable = false)
    private Boolean habilitado = true;

    public ConfiguracionSensor() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTipoSensor() { return tipoSensor; }
    public void setTipoSensor(String tipoSensor) { this.tipoSensor = tipoSensor; }

    public Boolean getHabilitado() { return habilitado; }
    public void setHabilitado(Boolean habilitado) { this.habilitado = habilitado; }
}