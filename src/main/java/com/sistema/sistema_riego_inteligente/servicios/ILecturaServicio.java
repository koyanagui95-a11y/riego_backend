package com.sistema.sistema_riego_inteligente.servicios;

import com.sistema.sistema_riego_inteligente.Entidad.Lectura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ILecturaServicio {
    Lectura guardar(Lectura nuevaLectura);
    Page<Lectura> obtenerTodo(Pageable pageable);
    Page<Lectura> obtenerPorUsuarioId(Long usuarioId, Pageable pageable);
    Lectura obtenerPorId(Long id);
    Lectura obtenerUltimoEstado();
    Lectura obtenerUltimoEstadoPorUsuario(Long usuarioId);
    void eliminar(Long id);
    void activarManual();
    void desactivarManual();
    boolean estaActivaManual();
    void registrarContactoEsp32();
    long obtenerUltimoContactoEsp32();
}