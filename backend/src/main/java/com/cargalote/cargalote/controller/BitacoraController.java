package com.cargalote.cargalote.controller;

import com.cargalote.cargalote.model.Bitacora;
import com.cargalote.cargalote.service.BitacoraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bitacora")
public class BitacoraController {

    private final BitacoraService bitacoraService;

    public BitacoraController(BitacoraService bitacoraService) {
        this.bitacoraService = bitacoraService;
    }

    @GetMapping
    public ResponseEntity<List<Bitacora>> listarBitacora() {
        return ResponseEntity.ok(bitacoraService.listarBitacora());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bitacora> buscarBitacora(@PathVariable Long id) {

        Bitacora bitacora = bitacoraService.buscarPorId(id);

        if (bitacora == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(bitacora);
    }

    @PostMapping
    public ResponseEntity<Bitacora> registrarBitacora(
            @Valid @RequestBody Bitacora bitacora) {

        Bitacora nuevaBitacora =
                bitacoraService.registrarBitacora(bitacora);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaBitacora);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bitacora> actualizarBitacora(
            @PathVariable Long id,
            @Valid @RequestBody Bitacora bitacora) {

        Bitacora bitacoraActualizada =
                bitacoraService.actualizarBitacora(id, bitacora);

        if (bitacoraActualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(bitacoraActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBitacora(@PathVariable Long id) {

        boolean eliminado = bitacoraService.eliminarBitacora(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}