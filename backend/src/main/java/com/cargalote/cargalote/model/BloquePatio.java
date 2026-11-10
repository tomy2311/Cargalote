package com.cargalote.cargalote.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "bloques_patio")
public class BloquePatio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank @Column(nullable = false)
    private String zona;

    @NotNull @Min(1)
    private Integer capacidad;

    @NotNull @Min(0)
    private Integer ocupados;

    @NotBlank @Column(nullable = false)
    private String estado;

    public BloquePatio() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }
    public Integer getOcupados() { return ocupados; }
    public void setOcupados(Integer ocupados) { this.ocupados = ocupados; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    @Transient
    public Integer getOcupacionPorcentaje() {
        if (capacidad == null || capacidad <= 0 || ocupados == null) return 0;
        return Math.round(ocupados * 100.0f / capacidad);
    }
}
