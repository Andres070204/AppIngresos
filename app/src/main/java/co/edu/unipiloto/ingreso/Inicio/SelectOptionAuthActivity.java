package co.edu.unipiloto.ingreso.Inicio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import co.edu.unipiloto.ingreso.R;

public class SelectOptionAuthActivity extends AppCompatActivity {

    Button btnCliente;
    Button btnConductor;
    Button btnPropietario;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_auth);
        mPref = getApplicationContext().getSharedPreferences("TypeUser",MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();

        btnCliente = findViewById(R.id.btnCliente);
        btnConductor = findViewById(R.id.btnConductor);
        btnPropietario = findViewById(R.id.btnPropietario);

        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user","Client");
                editor.apply();
                goToLogin();
            }
        });
    btnConductor.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editor.putString("user", "Driver");
            editor.apply();
            goToLogin();
        }
    });
    btnPropietario.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            editor.putString("user", "Proprietary");
            editor.apply();
            goToLogin();
        }
    });

    }

    private void goToLogin() {
        Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}