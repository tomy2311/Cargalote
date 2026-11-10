package com.cargalote.cargalote.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "operaciones_patio")
public class OperacionPatio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable = false)
    private String orden;

    @NotBlank @Column(nullable = false)
    private String tipoOperacion;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contenedor_id", nullable = false)
    private Contenedor contenedor;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bloque_id", nullable = false)
    private BloquePatio bloque;

    private LocalDateTime fechaProgramada;

    @NotBlank @Column(nullable = false)
    private String estado;

    public OperacionPatio() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrden() { return orden; }
    public void setOrden(String orden) { this.orden = orden; }
    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }
    public Contenedor getContenedor() { return contenedor; }
    public void setContenedor(Contenedor contenedor) { this.contenedor = contenedor; }
    public BloquePatio getBloque() { return bloque; }
    public void setBloque(BloquePatio bloque) { this.bloque = bloque; }
    public LocalDateTime getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDateTime fechaProgramada) { this.fechaProgramada = fechaProgramada; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
