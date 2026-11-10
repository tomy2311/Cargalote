package com.cargalote.cargalote.service;

import com.cargalote.cargalote.model.BloquePatio;
import com.cargalote.cargalote.repository.BloquePatioRepository;
import com.cargalote.cargalote.repository.OperacionPatioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BloquePatioService {
    private final BloquePatioRepository repository;
    private final OperacionPatioRepository operaciones;
    public BloquePatioService(BloquePatioRepository repository, OperacionPatioRepository operaciones) {
        this.repository = repository; this.operaciones = operaciones;
    }
    public List<BloquePatio> listar() { return repository.findAll(); }
    public BloquePatio buscar(Long id) { return repository.findById(id).orElse(null); }
    public BloquePatio crear(BloquePatio item) { validarOcupacion(item); item.setId(null); return repository.save(item); }
    public BloquePatio actualizar(Long id, BloquePatio datos) {
        BloquePatio item = repository.findById(id).orElse(null);
        if (item == null) return null;
        validarOcupacion(datos);
        item.setCodigo(datos.getCodigo()); item.setZona(datos.getZona());
        item.setCapacidad(datos.getCapacidad()); item.setOcupados(datos.getOcupados()); item.setEstado(datos.getEstado());
        return repository.save(item);
    }
    @Transactional
    public boolean eliminar(Long id) {
        BloquePatio item = repository.findById(id).orElse(null);
        if (item == null) return false;
        if (operaciones.existsByBloque_Id(id)) {
            throw new IllegalArgumentException("No se puede eliminar el bloque porque tiene operaciones asociadas.");
        }
        repository.delete(item); return true;
    }
    private void validarOcupacion(BloquePatio item) {
        if (item.getCapacidad() != null && item.getOcupados() != null && item.getOcupados() > item.getCapacidad()) {
            throw new IllegalArgumentException("Los contenedores ocupados no pueden superar la capacidad del bloque.");
        }
    }
}
