package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.model.Carga;
import com.cargalote.cargalote.service.CargaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargas")
public class CargaController {

    private final CargaService cargaService;

    public CargaController(CargaService cargaService) {
        this.cargaService = cargaService;
    }

    @GetMapping
    public ResponseEntity<List<Carga>> listarCargas() {
        return ResponseEntity.ok(cargaService.listarCargas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carga> buscarCarga(@PathVariable Long id) {

        Carga carga = cargaService.buscarPorId(id);

        if (carga == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(carga);
    }

    @PostMapping
    public ResponseEntity<Carga> registrarCarga(
            @Valid @RequestBody Carga carga) {

        Carga nuevaCarga = cargaService.registrarCarga(carga);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaCarga);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carga> actualizarCarga(
            @PathVariable Long id,
            @Valid @RequestBody Carga carga) {

        Carga cargaActualizada = cargaService.actualizarCarga(id, carga);

        if (cargaActualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cargaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCarga(@PathVariable Long id) {

        boolean eliminada = cargaService.eliminarCarga(id);

        if (!eliminada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}