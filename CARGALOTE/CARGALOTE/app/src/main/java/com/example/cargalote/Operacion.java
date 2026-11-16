package com.example.cargalote;

import java.io.Serializable;

public class Operacion implements Serializable {

    public final int id;
    public final String nombreCarga, idContenedor, ubicacion, fecha;
    public final String turno, horario, maquinaria, supervisor;
    public final EstadoOperacion estado;

    public Operacion(int id, String nombreCarga, String idContenedor,
                     EstadoOperacion estado, String ubicacion, String fecha,
                     String turno, String horario,
                     String maquinaria, String supervisor) {
        this.id = id;
        this.nombreCarga = nombreCarga;
        this.idContenedor = idContenedor;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.turno = turno;
        this.horario = horario;
        this.maquinaria = maquinaria;
        this.supervisor = supervisor;
    }
}