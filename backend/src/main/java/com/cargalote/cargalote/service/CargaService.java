package com.cargalote.cargalote.service;


import com.cargalote.cargalote.model.Carga;
import com.cargalote.cargalote.repository.CargaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CargaService {

    private final CargaRepository cargaRepository;

    public CargaService(CargaRepository cargaRepository) {
        this.cargaRepository = cargaRepository;
    }

    public List<Carga> listarCargas() {
        return cargaRepository.findAll();
    }

    public Carga buscarPorId(Long id) {
        Optional<Carga> carga = cargaRepository.findById(id);

        return carga.orElse(null);
    }

    public Carga registrarCarga(Carga carga) {
        return cargaRepository.save(carga);
    }

    public Carga actualizarCarga(Long id, Carga nuevosDatos) {

        Carga carga = buscarPorId(id);

        if (carga == null) {
            return null;
        }

        carga.setNumero(nuevosDatos.getNumero());
        carga.setOrigen(nuevosDatos.getOrigen());
        carga.setDestino(nuevosDatos.getDestino());
        carga.setEstado(nuevosDatos.getEstado());

        return cargaRepository.save(carga);
    }

    public boolean eliminarCarga(Long id) {

        Carga carga = buscarPorId(id);

        if (carga == null) {
            return false;
        }

        cargaRepository.deleteById(id);
        return true;
    }
}