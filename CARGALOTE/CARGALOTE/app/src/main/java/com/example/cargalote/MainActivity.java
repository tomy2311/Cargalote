package com.example.cargalote;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Identificación del empleado
        Empleado e = MockRepository.getEmpleado();
        ((TextView) findViewById(R.id.tvInicial))
                .setText(e.nombre.substring(0, 1).toUpperCase());
        ((TextView) findViewById(R.id.tvSaludo)).setText(e.nombre);
        ((TextView) findViewById(R.id.tvLegajo))
                .setText("Legajo " + e.legajo + " · " + e.cargo);

        findViewById(R.id.cardEmpleado).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)));

        // Operación de hoy
        List<Operacion> todas = MockRepository.getAsignaciones();
        Operacion hoy = todas.get(0);

        ((TextView) findViewById(R.id.tvSector)).setText(hoy.ubicacion);
        ((TextView) findViewById(R.id.tvTurno)).setText("Turno " + hoy.turno + " · " + hoy.horario);
        ((TextView) findViewById(R.id.tvMaquinaria)).setText("Maquinaria: " + hoy.maquinaria);
        ((TextView) findViewById(R.id.tvSupervisor)).setText("Supervisor: " + hoy.supervisor);
        EstadoUtil.aplicarBadge(findViewById(R.id.tvEstadoHoy), hoy.estado);

        // 6. Seleccionar la operación de hoy
        findViewById(R.id.cardHoy).setOnClickListener(v -> abrirDetalle(hoy));

        // 2, 3, 4, 5. Lista de operaciones
        List<Operacion> proximas = todas.subList(1, todas.size());
        ((TextView) findViewById(R.id.tvCantidad))
                .setText(proximas.size() + " próximas");

        RecyclerView rv = findViewById(R.id.rvProximas);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AsignacionAdapter(proximas, this::abrirDetalle));

        Button btnSalir = findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void abrirDetalle(Operacion a) {
        Intent i = new Intent(this, DetalleActivity.class);
        i.putExtra(DetalleActivity.EXTRA_ASIGNACION, a);
        startActivity(i);
    }
}