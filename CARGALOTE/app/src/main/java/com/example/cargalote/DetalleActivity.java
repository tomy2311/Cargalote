package com.example.cargalote;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetalleActivity extends AppCompatActivity {

    public static final String EXTRA_ASIGNACION = "asignacion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        Operacion a = (Operacion) getIntent().getSerializableExtra(EXTRA_ASIGNACION);

        if (a == null) {
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvDetDia)).setText(a.fecha);
        ((TextView) findViewById(R.id.tvDetSector)).setText(a.ubicacion);
        ((TextView) findViewById(R.id.tvDetCarga)).setText("Carga: " + a.nombreCarga);
        ((TextView) findViewById(R.id.tvDetContenedor)).setText("Contenedor: " + a.idContenedor);
        ((TextView) findViewById(R.id.tvDetTurno)).setText("Turno: " + a.turno);
        ((TextView) findViewById(R.id.tvDetHorario)).setText("Horario: " + a.horario);
        ((TextView) findViewById(R.id.tvDetMaquinaria)).setText("Maquinaria: " + a.maquinaria);
        ((TextView) findViewById(R.id.tvDetSupervisor)).setText("Supervisor: " + a.supervisor);

        ((Button) findViewById(R.id.btnVolver)).setOnClickListener(v -> finish());
    }
}