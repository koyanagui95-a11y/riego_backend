package com.sistema.sistema_riego_inteligente.servicios;

import com.sistema.sistema_riego_inteligente.Entidad.Lectura;
import com.sistema.sistema_riego_inteligente.Repositorios.LecturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class LecturaServicioImpl implements ILecturaServicio {

    @Autowired
    private LecturaRepository lecturaRepositorio;

    private static final long DURACION_MANUAL_MAXIMA_MS = 5 * 60 * 1000;

    private boolean bombaManual = false;
    private long inicioBombaManual = 0;
    private volatile long ultimoContactoEsp32 = 0;

    @Override
    public Lectura guardar(Lectura nuevaLectura) {
        Lectura ultimaLectura = null;
        if (nuevaLectura.getUsuario() != null) {
            ultimaLectura = lecturaRepositorio
                    .findTopByUsuarioIdOrderByIdDesc(nuevaLectura.getUsuario().getId())
                    .orElse(null);
        }

        if (ultimaLectura == null) {
            return lecturaRepositorio.save(nuevaLectura);
        }
        boolean bombaNueva = Boolean.TRUE.equals(nuevaLectura.getBombaActiva());
        boolean bombaUltima = Boolean.TRUE.equals(ultimaLectura.getBombaActiva());
        boolean cambioBomba = bombaNueva != bombaUltima;

        int humedadNueva = nuevaLectura.getHumedad_suelo();
        int humedadUltima = ultimaLectura.getHumedad_suelo();
        boolean cambioHumedadSignificativo = Math.abs(humedadNueva - humedadUltima) >= 15;

        boolean tiempoExcedido = false;
        if (ultimaLectura.getFechaRegistro() != null) {
            long segundos = Duration.between(
                    ultimaLectura.getFechaRegistro(),
                    LocalDateTime.now(ZoneId.of("America/Lima"))
            ).getSeconds();
            tiempoExcedido = segundos >= 300;
        }

        if (cambioBomba || cambioHumedadSignificativo || tiempoExcedido) {
            return lecturaRepositorio.save(nuevaLectura);
        }
        return nuevaLectura;
    }

    @Override
    public Page<Lectura> obtenerTodo(Pageable pageable) {
        return lecturaRepositorio.findAll(pageable);
    }

    @Override
    public Page<Lectura> obtenerPorUsuarioId(Long usuarioId, Pageable pageable) {
        return lecturaRepositorio.findByUsuarioId(usuarioId, pageable);
    }

    @Override
    public Lectura obtenerPorId(Long id) {
        return lecturaRepositorio.findById(id).orElse(null);
    }

    @Override
    public Lectura obtenerUltimoEstado() {
        return lecturaRepositorio.findTopByOrderByIdDesc().orElse(null);
    }

    @Override
    public Lectura obtenerUltimoEstadoPorUsuario(Long usuarioId) {
        return lecturaRepositorio.findTopByUsuarioIdOrderByIdDesc(usuarioId).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        lecturaRepositorio.deleteById(id);
    }

    @Override
    public void activarManual() {
        bombaManual = true;
        inicioBombaManual = System.currentTimeMillis();
    }

    @Override
    public void desactivarManual() {
        bombaManual = false;
        inicioBombaManual = 0;
    }

    @Override
    public boolean estaActivaManual() {
        if (bombaManual && inicioBombaManual > 0) {
            long transcurrido = System.currentTimeMillis() - inicioBombaManual;
            if (transcurrido >= DURACION_MANUAL_MAXIMA_MS) {
                bombaManual = false;
                inicioBombaManual = 0;
            }
        }
        return bombaManual;
    }

    @Override
    public void registrarContactoEsp32() {
        ultimoContactoEsp32 = System.currentTimeMillis();
    }

    @Override
    public long obtenerUltimoContactoEsp32() {
        return ultimoContactoEsp32;
    }
}