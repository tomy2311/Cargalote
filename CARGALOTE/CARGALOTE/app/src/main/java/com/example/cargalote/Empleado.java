package com.example.cargalote;

public class Empleado {

    public final String nombre, legajo, cargo, terminal;

    public Empleado(String nombre, String legajo, String cargo, String terminal) {
        this.nombre = nombre;
        this.legajo = legajo;
        this.cargo = cargo;
        this.terminal = terminal;
    }
}