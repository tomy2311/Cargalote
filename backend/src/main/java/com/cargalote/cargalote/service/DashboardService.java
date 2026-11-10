package com.cargalote.cargalote.service;

import com.cargalote.cargalote.dto.DashboardResumen;
import com.cargalote.cargalote.model.*;
import com.cargalote.cargalote.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class DashboardService {
    private final CargaRepository cargas;
    private final ContenedorRepository contenedores;
    private final EquipoRepository equipos;
    private final PersonalRepository personal;
    private final IncidenciaRepository incidencias;
    private final BuqueRepository buques;
    private final OperacionMuelleRepository operacionesMuelle;
    private final OperacionPatioRepository operacionesPatio;
    private final BloquePatioRepository bloques;

    public DashboardService(CargaRepository cargas, ContenedorRepository contenedores, EquipoRepository equipos,
            PersonalRepository personal, IncidenciaRepository incidencias, BuqueRepository buques,
            OperacionMuelleRepository operacionesMuelle, OperacionPatioRepository operacionesPatio,
            BloquePatioRepository bloques) {
        this.cargas = cargas; this.contenedores = contenedores; this.equipos = equipos; this.personal = personal;
        this.incidencias = incidencias; this.buques = buques; this.operacionesMuelle = operacionesMuelle;
        this.operacionesPatio = operacionesPatio; this.bloques = bloques;
    }

    @Transactional(readOnly = true)
    public DashboardResumen obtenerResumen() {
        var listaEquipos = equipos.findAll();
        var listaIncidencias = incidencias.findAll();
        var listaBuques = buques.findAll();
        var opsMuelle = operacionesMuelle.findAll();
        var opsPatio = operacionesPatio.findAll();
        var listaBloques = bloques.findAll();

        long equiposOperativos = listaEquipos.stream().filter(e -> contiene(e.getEstado(), "operat")).count();
        long incidenciasActivas = listaIncidencias.stream().filter(i -> !esFinal(i.getEstado())).count();
        long buquesActivos = listaBuques.stream().filter(b -> !esFinal(b.getEstado())).count();
        long opsMuelleActivas = opsMuelle.stream().filter(o -> !esFinal(o.getEstado())).count();
        long opsPatioPendientes = opsPatio.stream().filter(o -> !esFinal(o.getEstado())).count();
        long movimientosHora = opsMuelle.stream()
            .filter(o -> contiene(o.getEstado(), "curso") || contiene(o.getEstado(), "operat"))
            .mapToLong(o -> o.getMovimientosHora() == null ? 0 : o.getMovimientosHora()).sum();

        long capacidad = listaBloques.stream().mapToLong(b -> b.getCapacidad() == null ? 0 : b.getCapacidad()).sum();
        long ocupados = listaBloques.stream().mapToLong(b -> b.getOcupados() == null ? 0 : b.getOcupados()).sum();
        double ocupacion = capacidad == 0 ? 0.0 : Math.round(ocupados * 10000.0 / capacidad) / 100.0;

        return new DashboardResumen(cargas.count(), contenedores.count(), listaEquipos.size(), equiposOperativos,
            personal.count(), incidenciasActivas, buquesActivos, opsMuelleActivas, opsPatioPendientes,
            movimientosHora, ocupacion);
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(texto);
    }
    private boolean esFinal(String estado) {
        if (estado == null) return false;
        String valor = estado.toLowerCase(Locale.ROOT);
        return valor.contains("cerrad") || valor.contains("resuelt") || valor.contains("finaliz")
            || valor.contains("complet") || valor.contains("terminad") || valor.contains("despachad")
            || valor.contains("zarpad") || valor.contains("cancelad");
    }
}
