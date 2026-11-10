package com.cargalote.cargalote.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record OperacionPatioRequest(
    @NotBlank String orden,
    @NotBlank String tipoOperacion,
    @NotNull Long contenedorId,
    @NotNull Long bloqueId,
    LocalDateTime fechaProgramada,
    @NotBlank String estado
) {}
