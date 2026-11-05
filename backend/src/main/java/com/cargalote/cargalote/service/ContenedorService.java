package com.cargalote.cargalote.service;



import com.cargalote.cargalote.model.Contenedor;
import com.cargalote.cargalote.repository.ContenedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContenedorService {

    private final ContenedorRepository contenedorRepository;

    public ContenedorService(ContenedorRepository contenedorRepository) {
        this.contenedorRepository = contenedorRepository;
    }

    public List<Contenedor> listarContenedores() {
        return contenedorRepository.findAll();
    }

    public Contenedor buscarPorId(Long id) {

        Optional<Contenedor> contenedor = contenedorRepository.findById(id);

        return contenedor.orElse(null);
    }

    public Contenedor registrarContenedor(Contenedor contenedor) {
        return contenedorRepository.save(contenedor);
    }

    public Contenedor actualizarContenedor(Long id, Contenedor nuevosDatos) {

        Contenedor contenedor = buscarPorId(id);

        if (contenedor == null) {
            return null;
        }

        contenedor.setNumero(nuevosDatos.getNumero());
        contenedor.setTipo(nuevosDatos.getTipo());
        contenedor.setEstado(nuevosDatos.getEstado());
        contenedor.setUbicacion(nuevosDatos.getUbicacion());

        return contenedorRepository.save(contenedor);
    }

    public boolean eliminarContenedor(Long id) {

        Contenedor contenedor = buscarPorId(id);

        if (contenedor == null) {
            return false;
        }

        contenedorRepository.deleteById(id);
        return true;
    }
}