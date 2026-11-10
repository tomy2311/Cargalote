package com.cargalote.cargalote.service;

import com.cargalote.cargalote.dto.OperacionMuelleRequest;
import com.cargalote.cargalote.dto.OperacionMuelleResponse;
import com.cargalote.cargalote.model.*;
import com.cargalote.cargalote.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OperacionMuelleService {
    private final OperacionMuelleRepository repository;
    private final BuqueRepository buques;
    private final AtraqueRepository atraques;
    private final EquipoRepository equipos;

    public OperacionMuelleService(OperacionMuelleRepository repository, BuqueRepository buques,
                                  AtraqueRepository atraques, EquipoRepository equipos) {
        this.repository = repository; this.buques = buques; this.atraques = atraques; this.equipos = equipos;
    }
    @Transactional(readOnly = true)
    public List<OperacionMuelleResponse> listar() { return repository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public OperacionMuelleResponse buscar(Long id) {
        return repository.findById(id).map(this::toResponse).orElse(null);
    }
    @Transactional
    public OperacionMuelleResponse crear(OperacionMuelleRequest request) {
        OperacionMuelle item = new OperacionMuelle();
        aplicar(item, request);
        return toResponse(repository.save(item));
    }
    @Transactional
    public OperacionMuelleResponse actualizar(Long id, OperacionMuelleRequest request) {
        OperacionMuelle item = repository.findById(id).orElse(null);
        if (item == null) return null;
        aplicar(item, request);
        return toResponse(repository.save(item));
    }
    public boolean eliminar(Long id) {
        OperacionMuelle item = repository.findById(id).orElse(null);
        if (item == null) return false;
        repository.delete(item); return true;
    }
    private void aplicar(OperacionMuelle item, OperacionMuelleRequest request) {
        item.setBuque(buques.findById(request.buqueId())
            .orElseThrow(() -> new EntityNotFoundException("No existe el buque seleccionado.")));
        item.setAtraque(atraques.findById(request.atraqueId())
            .orElseThrow(() -> new EntityNotFoundException("No existe el atraque seleccionado.")));
        List<Long> ids = request.equiposIds() == null ? List.of() : request.equiposIds().stream().distinct().toList();
        List<Equipo> encontrados = equipos.findAllById(ids);
        if (encontrados.size() != ids.size()) throw new EntityNotFoundException("Uno o más equipos seleccionados no existen.");
        item.setEquipos(new LinkedHashSet<>(encontrados));
        item.setMovimientosHora(request.movimientosHora());
        item.setAvancePorcentaje(request.avancePorcentaje());
        item.setEstado(request.estado());
        item.setInicio(request.inicio());
    }
    private OperacionMuelleResponse toResponse(OperacionMuelle item) {
        List<Long> equipoIds = item.getEquipos().stream().map(Equipo::getId).toList();
        String equipoNombres = item.getEquipos().stream().map(Equipo::getNombre).collect(Collectors.joining(", "));
        return new OperacionMuelleResponse(item.getId(), item.getBuque().getId(), item.getBuque().getNombre(),
            item.getAtraque().getId(), item.getAtraque().getCodigo(), equipoIds, equipoNombres,
            item.getMovimientosHora(), item.getAvancePorcentaje(), item.getEstado(), item.getInicio());
    }
}
