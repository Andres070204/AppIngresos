package co.edu.unipiloto.ingreso.Inicio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.edu.unipiloto.ingreso.Clients.HomeActivityClients;
import co.edu.unipiloto.ingreso.Drivers.HomeActivityDrivers;
import co.edu.unipiloto.ingreso.R;

public class loginActivity extends AppCompatActivity {

    TextView lblCrearCuenta;
    EditText txtInputEmail, txtInputPassword;
    Button btnLogin;
    SharedPreferences mPref;
    FirebaseAuth mAuth;
    private ProgressDialog mProgressBar;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_login);
        mPref = getApplicationContext().getSharedPreferences("TypeUser", MODE_PRIVATE);
        String selectedUser = mPref.getString("user","");
        Toast.makeText(this,"El rol seleccionado es " + selectedUser, Toast.LENGTH_SHORT).show();
        txtInputEmail = findViewById(R.id.inputEmail);
        txtInputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnlogin);
        lblCrearCuenta = findViewById(R.id.txtNotieneCuenta);

        lblCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(loginActivity.this, RegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarCredenciales();
            }
        });

        mProgressBar = new ProgressDialog(loginActivity.this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void verificarCredenciales(){
        String email = txtInputEmail.getText().toString();
        String password = txtInputPassword.getText().toString();
        if(email.isEmpty() || !email.contains("@")){
            showError(txtInputEmail, "Email no valido");
        }else if(password.isEmpty()|| password.length()<7){
            showError(txtInputPassword, "Password invalida");
        }else{
            // Mostrar ProgressBar
            mProgressBar.setTitle("Login");
            mProgressBar.setMessage("Iniciando sesión, espere un momento..");
            mProgressBar.setCanceledOnTouchOutside(false);
            mProgressBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mProgressBar.dismiss(); // Ocultar ProgressBar
                    if (task.isSuccessful()){
                        checkUserRole(email);
                    }else{
                        // Mostrar mensaje de error al usuario
                        Toast.makeText(getApplicationContext(), "No se pudo iniciar sesión, verificar el correo o contraseña", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void checkUserRole(String email) {
        DatabaseReference userRef = mDatabase.child("Users");

        userRef.child("clients").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    startActivity(new Intent(loginActivity.this, HomeActivityClients.class));
                } else {
                    userRef.child("drivers").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                startActivity(new Intent(loginActivity.this, HomeActivityDrivers.class));
                            } else {
                                userRef.child("proprietary").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            startActivity(new Intent(loginActivity.this,HomeActivity.class));
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Rol de usuario no identificado", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle possible errors.
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle possible errors.
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void showError(EditText input, String s){
        input.setError(s);
        input.requestFocus();
    }
}
