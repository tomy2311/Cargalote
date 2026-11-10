package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.dto.OperacionMuelleRequest;
import com.cargalote.cargalote.dto.OperacionMuelleResponse;
import com.cargalote.cargalote.service.OperacionMuelleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/operaciones-muelle")
public class OperacionMuelleController {
    private final OperacionMuelleService service;
    public OperacionMuelleController(OperacionMuelleService service) { this.service = service; }
    @GetMapping public ResponseEntity<List<OperacionMuelleResponse>> listar() { return ResponseEntity.ok(service.listar()); }
    @GetMapping("/{id}") public ResponseEntity<OperacionMuelleResponse> buscar(@PathVariable Long id) {
        OperacionMuelleResponse item = service.buscar(id); return item == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(item);
    }
    @PostMapping public ResponseEntity<OperacionMuelleResponse> crear(@Valid @RequestBody OperacionMuelleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }
    @PutMapping("/{id}") public ResponseEntity<OperacionMuelleResponse> actualizar(@PathVariable Long id, @Valid @RequestBody OperacionMuelleRequest request) {
        OperacionMuelleResponse item = service.actualizar(id, request); return item == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(item);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
