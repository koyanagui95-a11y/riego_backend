package com.sistema.sistema_riego_inteligente.Entidad;

import com.sistema.sistema_riego_inteligente.config.JsonMapConverter;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

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

    @Column(length = 100)
    private String label;

    @Column(length = 20)
    private String unidad;

    @Column(length = 20)
    private String color;

    @Column(length = 100)
    private String campo;

    @Column(name = "tipo_valor", length = 20)
    private String tipoValor;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> valores = new HashMap<>();

    private Integer orden;

    public ConfiguracionSensor() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTipoSensor() { return tipoSensor; }
    public void setTipoSensor(String tipoSensor) { this.tipoSensor = tipoSensor; }

    public Boolean getHabilitado() { return habilitado; }
    public void setHabilitado(Boolean habilitado) { this.habilitado = habilitado; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }

    public String getTipoValor() { return tipoValor; }
    public void setTipoValor(String tipoValor) { this.tipoValor = tipoValor; }

    public Map<String, Object> getValores() { return valores; }
    public void setValores(Map<String, Object> valores) { this.valores = valores; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}