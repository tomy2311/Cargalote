package com.cargalote.cargalote.service;

import com.cargalote.cargalote.dto.OperacionPatioRequest;
import com.cargalote.cargalote.dto.OperacionPatioResponse;
import com.cargalote.cargalote.model.*;
import com.cargalote.cargalote.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OperacionPatioService {
    private final OperacionPatioRepository repository;
    private final ContenedorRepository contenedores;
    private final BloquePatioRepository bloques;
    public OperacionPatioService(OperacionPatioRepository repository, ContenedorRepository contenedores,
                                 BloquePatioRepository bloques) {
        this.repository = repository; this.contenedores = contenedores; this.bloques = bloques;
    }
    @Transactional(readOnly = true)
    public List<OperacionPatioResponse> listar() { return repository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public OperacionPatioResponse buscar(Long id) { return repository.findById(id).map(this::toResponse).orElse(null); }
    @Transactional
    public OperacionPatioResponse crear(OperacionPatioRequest request) {
        OperacionPatio item = new OperacionPatio(); aplicar(item, request); return toResponse(repository.save(item));
    }
    @Transactional
    public OperacionPatioResponse actualizar(Long id, OperacionPatioRequest request) {
        OperacionPatio item = repository.findById(id).orElse(null); if (item == null) return null;
        aplicar(item, request); return toResponse(repository.save(item));
    }
    public boolean eliminar(Long id) { OperacionPatio item = repository.findById(id).orElse(null); if (item == null) return false; repository.delete(item); return true; }
    private void aplicar(OperacionPatio item, OperacionPatioRequest request) {
        Contenedor contenedor = contenedores.findById(request.contenedorId())
            .orElseThrow(() -> new EntityNotFoundException("No existe el contenedor seleccionado."));
        BloquePatio bloque = bloques.findById(request.bloqueId())
            .orElseThrow(() -> new EntityNotFoundException("No existe el bloque seleccionado."));
        item.setOrden(request.orden()); item.setTipoOperacion(request.tipoOperacion());
        item.setContenedor(contenedor); item.setBloque(bloque);
        item.setFechaProgramada(request.fechaProgramada()); item.setEstado(request.estado());
        // La ubicación del contenedor se edita desde el registro de Contenedores.
        // La operación de patio conserva la relación con el bloque sin cambiarla al programarse.
    }
    private OperacionPatioResponse toResponse(OperacionPatio item) {
        return new OperacionPatioResponse(item.getId(), item.getOrden(), item.getTipoOperacion(),
            item.getContenedor().getId(), item.getContenedor().getNumero(), item.getBloque().getId(),
            item.getBloque().getCodigo(), item.getFechaProgramada(), item.getEstado());
    }
}
