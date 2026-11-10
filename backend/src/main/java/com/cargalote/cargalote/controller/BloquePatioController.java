package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.model.BloquePatio;
import com.cargalote.cargalote.service.BloquePatioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bloques-patio")
public class BloquePatioController {
    private final BloquePatioService service;
    public BloquePatioController(BloquePatioService service) { this.service = service; }
    @GetMapping public ResponseEntity<List<BloquePatio>> listar() { return ResponseEntity.ok(service.listar()); }
    @GetMapping("/{id}") public ResponseEntity<BloquePatio> buscar(@PathVariable Long id) {
        BloquePatio item = service.buscar(id); return item == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(item);
    }
    @PostMapping public ResponseEntity<BloquePatio> crear(@Valid @RequestBody BloquePatio item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(item));
    }
    @PutMapping("/{id}") public ResponseEntity<BloquePatio> actualizar(@PathVariable Long id, @Valid @RequestBody BloquePatio item) {
        BloquePatio actualizado = service.actualizar(id, item); return actualizado == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(actualizado);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
