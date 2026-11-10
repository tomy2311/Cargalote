package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.dto.OperacionPatioRequest;
import com.cargalote.cargalote.dto.OperacionPatioResponse;
import com.cargalote.cargalote.service.OperacionPatioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/operaciones-patio")
public class OperacionPatioController {
    private final OperacionPatioService service;
    public OperacionPatioController(OperacionPatioService service) { this.service = service; }
    @GetMapping public ResponseEntity<List<OperacionPatioResponse>> listar() { return ResponseEntity.ok(service.listar()); }
    @GetMapping("/{id}") public ResponseEntity<OperacionPatioResponse> buscar(@PathVariable Long id) {
        OperacionPatioResponse item = service.buscar(id); return item == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(item);
    }
    @PostMapping public ResponseEntity<OperacionPatioResponse> crear(@Valid @RequestBody OperacionPatioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }
    @PutMapping("/{id}") public ResponseEntity<OperacionPatioResponse> actualizar(@PathVariable Long id, @Valid @RequestBody OperacionPatioRequest request) {
        OperacionPatioResponse item = service.actualizar(id, request); return item == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(item);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
