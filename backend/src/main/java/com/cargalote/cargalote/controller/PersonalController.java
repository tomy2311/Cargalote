package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.model.Personal;
import com.cargalote.cargalote.service.PersonalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal")
public class PersonalController {

    private final PersonalService personalService;

    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    @GetMapping
    public ResponseEntity<List<Personal>> listarPersonal() {
        return ResponseEntity.ok(personalService.listarPersonal());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Personal> buscarPersonal(@PathVariable Long id) {

        Personal persona = personalService.buscarPorId(id);

        if (persona == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(persona);
    }

    @PostMapping
    public ResponseEntity<Personal> registrarPersonal(
            @Valid @RequestBody Personal persona) {

        Personal nuevoPersonal =
                personalService.registrarPersonal(persona);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevoPersonal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Personal> actualizarPersonal(
            @PathVariable Long id,
            @Valid @RequestBody Personal persona) {

        Personal personalActualizado =
                personalService.actualizarPersonal(id, persona);

        if (personalActualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(personalActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPersonal(@PathVariable Long id) {

        boolean eliminado = personalService.eliminarPersonal(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}