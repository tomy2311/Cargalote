package com.cargalote.cargalote.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record OperacionMuelleRequest(
    @NotNull Long buqueId,
    @NotNull Long atraqueId,
    List<Long> equiposIds,
    @NotNull @Min(0) Integer movimientosHora,
    @NotNull @Min(0) @Max(100) Integer avancePorcentaje,
    @NotBlank String estado,
    LocalDateTime inicio
) {}
