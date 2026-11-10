package com.cargalote.cargalote.service;

import com.cargalote.cargalote.model.Buque;
import com.cargalote.cargalote.repository.BuqueRepository;
import com.cargalote.cargalote.repository.OperacionMuelleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BuqueService {
    private final BuqueRepository repository;
    private final OperacionMuelleRepository operaciones;
    public BuqueService(BuqueRepository repository, OperacionMuelleRepository operaciones) {
        this.repository = repository; this.operaciones = operaciones;
    }
    public List<Buque> listar() { return repository.findAll(); }
    public Buque buscar(Long id) { return repository.findById(id).orElse(null); }
    public Buque crear(Buque item) { item.setId(null); return repository.save(item); }
    public Buque actualizar(Long id, Buque datos) {
        Buque item = repository.findById(id).orElse(null);
        if (item == null) return null;
        item.setNombre(datos.getNombre()); item.setImo(datos.getImo());
        item.setNaviera(datos.getNaviera()); item.setOrigen(datos.getOrigen());
        item.setDestino(datos.getDestino()); item.setEta(datos.getEta()); item.setEstado(datos.getEstado());
        return repository.save(item);
    }
    @Transactional
    public boolean eliminar(Long id) {
        Buque item = repository.findById(id).orElse(null);
        if (item == null) return false;
        if (operaciones.existsByBuque_Id(id)) {
            throw new IllegalArgumentException("No se puede eliminar el buque porque tiene operaciones asociadas.");
        }
        repository.delete(item); return true;
    }
}
