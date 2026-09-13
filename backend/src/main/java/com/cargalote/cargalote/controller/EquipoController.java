package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.model.Equipo;
import com.cargalote.cargalote.service.EquipoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @GetMapping
    public ResponseEntity<List<Equipo>> listarEquipos() {
        return ResponseEntity.ok(equipoService.listarEquipos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipo> buscarEquipo(@PathVariable Long id) {

        Equipo equipo = equipoService.buscarPorId(id);

        if (equipo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(equipo);
    }

    @PostMapping
    public ResponseEntity<Equipo> registrarEquipo(
            @RequestBody Equipo equipo) {

        Equipo nuevoEquipo = equipoService.registrarEquipo(equipo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoEquipo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipo> actualizarEquipo(
            @PathVariable Long id,
            @RequestBody Equipo equipo) {

        Equipo equipoActualizado =
                equipoService.actualizarEquipo(id, equipo);

        if (equipoActualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(equipoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEquipo(@PathVariable Long id) {

        boolean eliminado = equipoService.eliminarEquipo(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}