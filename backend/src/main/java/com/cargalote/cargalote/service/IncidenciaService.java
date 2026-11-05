package com.cargalote.cargalote.service;


import com.cargalote.cargalote.model.Incidencia;
import com.cargalote.cargalote.repository.IncidenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;

    public IncidenciaService(IncidenciaRepository incidenciaRepository) {
        this.incidenciaRepository = incidenciaRepository;
    }

    public List<Incidencia> listarIncidencias() {
        return incidenciaRepository.findAll();
    }

    public Incidencia buscarPorId(Long id) {

        Optional<Incidencia> incidencia = incidenciaRepository.findById(id);

        return incidencia.orElse(null);
    }

    public Incidencia registrarIncidencia(Incidencia incidencia) {
        return incidenciaRepository.save(incidencia);
    }

    public Incidencia actualizarIncidencia(Long id, Incidencia nuevosDatos) {

        Incidencia incidencia = buscarPorId(id);

        if (incidencia == null) {
            return null;
        }

        incidencia.setHora(nuevosDatos.getHora());
        incidencia.setSeveridad(nuevosDatos.getSeveridad());
        incidencia.setDescripcion(nuevosDatos.getDescripcion());
        incidencia.setZona(nuevosDatos.getZona());
        incidencia.setEstado(nuevosDatos.getEstado());

        return incidenciaRepository.save(incidencia);
    }

    public boolean eliminarIncidencia(Long id) {

        Incidencia incidencia = buscarPorId(id);

        if (incidencia == null) {
            return false;
        }

        incidenciaRepository.deleteById(id);
        return true;
    }
}