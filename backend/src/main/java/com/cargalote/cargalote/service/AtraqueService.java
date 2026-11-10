package com.cargalote.cargalote.service;

import com.cargalote.cargalote.model.Atraque;
import com.cargalote.cargalote.repository.AtraqueRepository;
import com.cargalote.cargalote.repository.OperacionMuelleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AtraqueService {
    private final AtraqueRepository repository;
    private final OperacionMuelleRepository operaciones;
    public AtraqueService(AtraqueRepository repository, OperacionMuelleRepository operaciones) {
        this.repository = repository; this.operaciones = operaciones;
    }
    public List<Atraque> listar() { return repository.findAll(); }
    public Atraque buscar(Long id) { return repository.findById(id).orElse(null); }
    public Atraque crear(Atraque item) { item.setId(null); return repository.save(item); }
    public Atraque actualizar(Long id, Atraque datos) {
        Atraque item = repository.findById(id).orElse(null);
        if (item == null) return null;
        item.setCodigo(datos.getCodigo()); item.setMuelle(datos.getMuelle());
        item.setLongitudMetros(datos.getLongitudMetros()); item.setEstado(datos.getEstado());
        return repository.save(item);
    }
    @Transactional
    public boolean eliminar(Long id) {
        Atraque item = repository.findById(id).orElse(null);
        if (item == null) return false;
        if (operaciones.existsByAtraque_Id(id)) {
            throw new IllegalArgumentException("No se puede eliminar el atraque porque tiene operaciones asociadas.");
        }
        repository.delete(item); return true;
    }
}
