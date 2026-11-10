package com.cargalote.cargalote.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "atraques")
public class Atraque {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable = false, unique = true)
    private String codigo;

    @NotBlank @Column(nullable = false)
    private String muelle;

    @NotNull @Min(1)
    private Integer longitudMetros;

    @NotBlank @Column(nullable = false)
    private String estado;

    public Atraque() {}
    public Atraque(Long id, String codigo, String muelle, Integer longitudMetros, String estado) {
        this.id = id; this.codigo = codigo; this.muelle = muelle;
        this.longitudMetros = longitudMetros; this.estado = estado;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getMuelle() { return muelle; }
    public void setMuelle(String muelle) { this.muelle = muelle; }
    public Integer getLongitudMetros() { return longitudMetros; }
    public void setLongitudMetros(Integer longitudMetros) { this.longitudMetros = longitudMetros; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
