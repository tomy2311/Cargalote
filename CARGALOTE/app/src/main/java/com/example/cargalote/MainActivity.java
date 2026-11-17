package com.example.cargalote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        mostrarEmpleado();
        mostrarOperaciones();

        ((Button) findViewById(R.id.btnSalir)).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void mostrarEmpleado() {
        Empleado e = MockRepository.getEmpleado();
        ((TextView) findViewById(R.id.tvInicial)).setText(e.nombre.substring(0, 1).toUpperCase());
        ((TextView) findViewById(R.id.tvSaludo)).setText(e.nombre);
        ((TextView) findViewById(R.id.tvLegajo)).setText("Legajo " + e.legajo + " · " + e.cargo);
        findViewById(R.id.cardEmpleado).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)));
    }

    private void mostrarOperaciones() {
        List<Operacion> todas = MockRepository.getAsignaciones();

        View cardHoy = findViewById(R.id.cardHoy);
        RecyclerView rv = findViewById(R.id.rvProximas);
        TextView tvVacio = findViewById(R.id.tvVacio);
        TextView tvCantidad = findViewById(R.id.tvCantidad);

        // Lista vacía: se oculta todo y se muestra el aviso
        if (todas.isEmpty()) {
            cardHoy.setVisibility(View.GONE);
            rv.setVisibility(View.GONE);
            tvCantidad.setText("");
            tvVacio.setVisibility(View.VISIBLE);
            return;
        }

        // Operación de hoy (la primera)
        Operacion hoy = todas.get(0);
        ((TextView) findViewById(R.id.tvSector)).setText(hoy.nombreCarga + " · " + hoy.ubicacion);
        ((TextView) findViewById(R.id.tvTurno)).setText("Turno " + hoy.turno + " · " + hoy.horario);
        ((TextView) findViewById(R.id.tvMaquinaria)).setText("Maquinaria: " + hoy.maquinaria);
        ((TextView) findViewById(R.id.tvSupervisor)).setText("Supervisor: " + hoy.supervisor);
        EstadoUtil.aplicarBadge(findViewById(R.id.tvEstadoHoy), hoy.estado);
        cardHoy.setOnClickListener(v -> abrirDetalle(hoy));

        // Listado: el resto de las operaciones
        List<Operacion> proximas = todas.subList(1, todas.size());
        tvCantidad.setText(proximas.size() + " próximas");

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AsignacionAdapter(proximas, this::abrirDetalle));

        boolean hayProximas = !proximas.isEmpty();
        rv.setVisibility(hayProximas ? View.VISIBLE : View.GONE);
        tvVacio.setText("No hay más operaciones próximas");
        tvVacio.setVisibility(hayProximas ? View.GONE : View.VISIBLE);
    }

    private void abrirDetalle(Operacion o) {
        Intent i = new Intent(this, DetalleActivity.class);
        i.putExtra(DetalleActivity.EXTRA_ASIGNACION, o);
        startActivity(i);
    }
}