package com.example.cargalote;

public enum EstadoOperacion {
    PENDIENTE("Pendiente", R.color.estado_pendiente),
    EN_CURSO("En curso", R.color.estado_curso),
    COMPLETADA("Completada", R.color.estado_completada);

    public final String etiqueta;
    public final int colorRes;

    EstadoOperacion(String etiqueta, int colorRes) {
        this.etiqueta = etiqueta;
        this.colorRes = colorRes;
    }
}