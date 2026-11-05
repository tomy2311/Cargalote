package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.model.Incidencia;
import com.cargalote.cargalote.service.IncidenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    @GetMapping
    public ResponseEntity<List<Incidencia>> listarIncidencias() {
        return ResponseEntity.ok(incidenciaService.listarIncidencias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incidencia> buscarIncidencia(@PathVariable Long id) {

        Incidencia incidencia = incidenciaService.buscarPorId(id);

        if (incidencia == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(incidencia);
    }

    @PostMapping
    public ResponseEntity<Incidencia> registrarIncidencia(
            @Valid @RequestBody Incidencia incidencia) {

        Incidencia nuevaIncidencia =
                incidenciaService.registrarIncidencia(incidencia);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaIncidencia);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incidencia> actualizarIncidencia(
            @PathVariable Long id,
            @Valid @RequestBody Incidencia incidencia) {

        Incidencia incidenciaActualizada =
                incidenciaService.actualizarIncidencia(id, incidencia);

        if (incidenciaActualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(incidenciaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIncidencia(@PathVariable Long id) {

        boolean eliminado = incidenciaService.eliminarIncidencia(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}