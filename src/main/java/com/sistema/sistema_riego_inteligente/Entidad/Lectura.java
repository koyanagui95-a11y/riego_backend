package com.sistema.sistema_riego_inteligente.Entidad;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
    private Integer humedad_ambiente;
    
    @Column(nullable = false)
    private Integer temperatura;

    @Column(nullable = false)
    private Double flujoAgua;

    @Column(nullable = false)
    private Boolean bombaActiva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
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
    
    public Integer getHumedad_ambiente() { return humedad_ambiente; }
    public void setHumedad_ambiente(Integer humedad_ambiente) { this.humedad_ambiente = humedad_ambiente; }
    
    public Integer getTemperatura() { return temperatura; }
    public void setTemperatura(Integer temperatura) { this.temperatura = temperatura; }

    public Double getFlujoAgua() { return flujoAgua; }
    public void setFlujoAgua(Double flujoAgua) { this.flujoAgua = flujoAgua; }

    public Boolean getBombaActiva() { return bombaActiva; }
    public void setBombaActiva(Boolean bombaActiva) { this.bombaActiva = bombaActiva; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    
}