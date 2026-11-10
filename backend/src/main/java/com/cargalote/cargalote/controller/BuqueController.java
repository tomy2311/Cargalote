package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.model.Buque;
import com.cargalote.cargalote.service.BuqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buques")
public class BuqueController {
    private final BuqueService service;
    public BuqueController(BuqueService service) { this.service = service; }
    @GetMapping public ResponseEntity<List<Buque>> listar() { return ResponseEntity.ok(service.listar()); }
    @GetMapping("/{id}") public ResponseEntity<Buque> buscar(@PathVariable Long id) {
        Buque item = service.buscar(id); return item == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(item);
    }
    @PostMapping public ResponseEntity<Buque> crear(@Valid @RequestBody Buque item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(item));
    }
    @PutMapping("/{id}") public ResponseEntity<Buque> actualizar(@PathVariable Long id, @Valid @RequestBody Buque item) {
        Buque actualizado = service.actualizar(id, item); return actualizado == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(actualizado);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
