package com.cargalote.cargalote.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "operaciones_muelle")
public class OperacionMuelle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buque_id", nullable = false)
    private Buque buque;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atraque_id", nullable = false)
    private Atraque atraque;

    @ManyToMany
    @JoinTable(name = "operacion_muelle_equipos",
        joinColumns = @JoinColumn(name = "operacion_id"),
        inverseJoinColumns = @JoinColumn(name = "equipo_id"))
    private Set<Equipo> equipos = new LinkedHashSet<>();

    @NotNull @Min(0)
    private Integer movimientosHora;

    @NotNull @Min(0) @Max(100)
    private Integer avancePorcentaje;

    @NotBlank @Column(nullable = false)
    private String estado;

    private LocalDateTime inicio;

    public OperacionMuelle() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Buque getBuque() { return buque; }
    public void setBuque(Buque buque) { this.buque = buque; }
    public Atraque getAtraque() { return atraque; }
    public void setAtraque(Atraque atraque) { this.atraque = atraque; }
    public Set<Equipo> getEquipos() { return equipos; }
    public void setEquipos(Set<Equipo> equipos) { this.equipos = equipos; }
    public Integer getMovimientosHora() { return movimientosHora; }
    public void setMovimientosHora(Integer movimientosHora) { this.movimientosHora = movimientosHora; }
    public Integer getAvancePorcentaje() { return avancePorcentaje; }
    public void setAvancePorcentaje(Integer avancePorcentaje) { this.avancePorcentaje = avancePorcentaje; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
}
