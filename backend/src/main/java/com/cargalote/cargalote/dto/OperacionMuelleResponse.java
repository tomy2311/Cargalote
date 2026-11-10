package com.cargalote.cargalote.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OperacionMuelleResponse(
    Long id,
    Long buqueId,
    String buqueNombre,
    Long atraqueId,
    String atraqueCodigo,
    List<Long> equiposIds,
    String equiposNombres,
    Integer movimientosHora,
    Integer avancePorcentaje,
    String estado,
    LocalDateTime inicio
) {}
