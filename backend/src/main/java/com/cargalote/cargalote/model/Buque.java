package com.cargalote.cargalote.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "buques")
public class Buque {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable = false)
    private String nombre;

    private String imo;

    @NotBlank @Column(nullable = false)
    private String naviera;

    @NotBlank @Column(nullable = false)
    private String origen;

    @NotBlank @Column(nullable = false)
    private String destino;

    private LocalDateTime eta;

    @NotBlank @Column(nullable = false)
    private String estado;

    public Buque() {}
    public Buque(Long id, String nombre, String imo, String naviera, String origen,
                 String destino, LocalDateTime eta, String estado) {
        this.id = id; this.nombre = nombre; this.imo = imo; this.naviera = naviera;
        this.origen = origen; this.destino = destino; this.eta = eta; this.estado = estado;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getImo() { return imo; }
    public void setImo(String imo) { this.imo = imo; }
    public String getNaviera() { return naviera; }
    public void setNaviera(String naviera) { this.naviera = naviera; }
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public LocalDateTime getEta() { return eta; }
    public void setEta(LocalDateTime eta) { this.eta = eta; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
