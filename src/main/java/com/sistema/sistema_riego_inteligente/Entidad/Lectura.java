package com.sistema.sistema_riego_inteligente.Entidad;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sistema.sistema_riego_inteligente.config.JsonMapConverter;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "lecturas")
public class Lectura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer humedad_suelo;

    @Column(nullable = false)
    private Double humedad_ambiente;

    @Column(nullable = false)
    private Double temperatura;

    @Column(nullable = false)
    private Double flujoAgua;

    @Column(nullable = false)
    private Boolean bombaActiva;

    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> extras = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now(ZoneId.of("America/Lima"));
    }

    public Lectura() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getHumedad_suelo() { return humedad_suelo; }
    public void setHumedad_suelo(Integer humedad_suelo) { this.humedad_suelo = humedad_suelo; }

    public Double getHumedad_ambiente() { return humedad_ambiente; }
    public void setHumedad_ambiente(Double humedad_ambiente) { this.humedad_ambiente = humedad_ambiente; }

    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }

    public Double getFlujoAgua() { return flujoAgua; }
    public void setFlujoAgua(Double flujoAgua) { this.flujoAgua = flujoAgua; }

    public Boolean getBombaActiva() { return bombaActiva; }
    public void setBombaActiva(Boolean bombaActiva) { this.bombaActiva = bombaActiva; }

    public Map<String, Object> getExtras() { return extras; }
    public void setExtras(Map<String, Object> extras) { this.extras = extras; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}