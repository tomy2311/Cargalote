package com.cargalote.cargalote.service;


import com.cargalote.cargalote.model.Equipo;
import com.cargalote.cargalote.repository.EquipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;

    public EquipoService(EquipoRepository equipoRepository) {
        this.equipoRepository = equipoRepository;
    }

    public List<Equipo> listarEquipos() {
        return equipoRepository.findAll();
    }

    public Equipo buscarPorId(Long id) {

        Optional<Equipo> equipo = equipoRepository.findById(id);

        return equipo.orElse(null);
    }

    public Equipo registrarEquipo(Equipo equipo) {
        return equipoRepository.save(equipo);
    }

    public Equipo actualizarEquipo(Long id, Equipo nuevosDatos) {

        Equipo equipo = buscarPorId(id);

        if (equipo == null) {
            return null;
        }

        equipo.setNombre(nuevosDatos.getNombre());
        equipo.setTipo(nuevosDatos.getTipo());
        equipo.setEstado(nuevosDatos.getEstado());
        equipo.setHorasUso(nuevosDatos.getHorasUso());

        return equipoRepository.save(equipo);
    }

    public boolean eliminarEquipo(Long id) {

        Equipo equipo = buscarPorId(id);

        if (equipo == null) {
            return false;
        }

        equipoRepository.deleteById(id);
        return true;
    }
}