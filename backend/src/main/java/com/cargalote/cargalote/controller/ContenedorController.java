package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.model.Contenedor;
import com.cargalote.cargalote.service.ContenedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenedores")
public class ContenedorController {

    private final ContenedorService contenedorService;

    public ContenedorController(ContenedorService contenedorService) {
        this.contenedorService = contenedorService;
    }

    @GetMapping
    public ResponseEntity<List<Contenedor>> listarContenedores() {
        return ResponseEntity.ok(contenedorService.listarContenedores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contenedor> buscarContenedor(@PathVariable Long id) {

        Contenedor contenedor = contenedorService.buscarPorId(id);

        if (contenedor == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(contenedor);
    }

    @PostMapping
    public ResponseEntity<Contenedor> registrarContenedor(
            @Valid @RequestBody Contenedor contenedor) {

        Contenedor nuevoContenedor =
                contenedorService.registrarContenedor(contenedor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoContenedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contenedor> actualizarContenedor(
            @PathVariable Long id,
            @Valid @RequestBody Contenedor contenedor) {

        Contenedor contenedorActualizado =
                contenedorService.actualizarContenedor(id, contenedor);

        if (contenedorActualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(contenedorActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContenedor(@PathVariable Long id) {

        boolean eliminado = contenedorService.eliminarContenedor(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}