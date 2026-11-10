package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.model.Atraque;
import com.cargalote.cargalote.service.AtraqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/atraques")
public class AtraqueController {
    private final AtraqueService service;
    public AtraqueController(AtraqueService service) { this.service = service; }
    @GetMapping public ResponseEntity<List<Atraque>> listar() { return ResponseEntity.ok(service.listar()); }
    @GetMapping("/{id}") public ResponseEntity<Atraque> buscar(@PathVariable Long id) {
        Atraque item = service.buscar(id); return item == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(item);
    }
    @PostMapping public ResponseEntity<Atraque> crear(@Valid @RequestBody Atraque item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(item));
    }
    @PutMapping("/{id}") public ResponseEntity<Atraque> actualizar(@PathVariable Long id, @Valid @RequestBody Atraque item) {
        Atraque actualizado = service.actualizar(id, item); return actualizado == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(actualizado);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
