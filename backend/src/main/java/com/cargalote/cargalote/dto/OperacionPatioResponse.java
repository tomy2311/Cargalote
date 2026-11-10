package com.cargalote.cargalote.dto;

import java.time.LocalDateTime;

public record OperacionPatioResponse(
    Long id,
    String orden,
    String tipoOperacion,
    Long contenedorId,
    String contenedorNumero,
    Long bloqueId,
    String bloqueCodigo,
    LocalDateTime fechaProgramada,
    String estado
) {}
