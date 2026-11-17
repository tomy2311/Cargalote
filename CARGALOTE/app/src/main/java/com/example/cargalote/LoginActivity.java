package com.example.cargalote;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etLegajo = findViewById(R.id.etLegajo);
        EditText etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String legajo = etLegajo.getText().toString().trim();
            String pass = etPass.getText().toString();

            if (MockRepository.login(legajo, pass)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Datos incorrectos (probá 1234 / demo)",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}