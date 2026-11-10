package com.cargalote.cargalote.dto;

public record DashboardResumen(
    long totalCargas,
    long totalContenedores,
    long totalEquipos,
    long equiposOperativos,
    long totalPersonal,
    long incidenciasActivas,
    long buquesActivos,
    long operacionesMuelleActivas,
    long operacionesPatioPendientes,
    long movimientosPorHora,
    double ocupacionPatioPorcentaje
) {}
