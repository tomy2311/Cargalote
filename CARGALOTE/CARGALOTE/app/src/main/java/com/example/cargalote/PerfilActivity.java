package com.example.cargalote;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Empleado e = MockRepository.getEmpleado();
        Operacion hoy = MockRepository.getHoy();

        ((TextView) findViewById(R.id.tvInicial)).setText(e.nombre.substring(0, 1).toUpperCase());
        ((TextView) findViewById(R.id.tvPerfilNombre)).setText(e.nombre);
        ((TextView) findViewById(R.id.tvPerfilCargo)).setText(e.cargo);
        ((TextView) findViewById(R.id.tvPerfilLegajo)).setText(e.legajo);
        ((TextView) findViewById(R.id.tvPerfilMaquinaria)).setText(hoy.maquinaria);
        ((TextView) findViewById(R.id.tvPerfilTurno)).setText(hoy.turno + " · " + hoy.horario);
        ((TextView) findViewById(R.id.tvPerfilTerminal)).setText(e.terminal);

        ((Button) findViewById(R.id.btnPerfilVolver)).setOnClickListener(v -> finish());
    }
}