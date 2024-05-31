package co.edu.unipiloto.ingreso.Inicio;




        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

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
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;

        import co.edu.unipiloto.ingreso.R;
        import co.edu.unipiloto.ingreso.User;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences mPref;

    TextView tieneCuenta;
    Button btnRegistrar;
    EditText txtInputUsername, txtInputEmail, txtInputPassword, txtInputConfirmPassword;
    private ProgressDialog mProgressBar;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtInputUsername = findViewById(R.id.inputUsername);
        txtInputEmail = findViewById(R.id.inputEmail);
        txtInputPassword = findViewById(R.id.inputPassword);
        txtInputConfirmPassword = findViewById(R.id.inputConfirmPassword);

        btnRegistrar = findViewById(R.id.btnRegister);
        tieneCuenta =findViewById(R.id.alreadyHaveAccount);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarCredenciales();
            }
        });

        tieneCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, loginActivity.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPref = getApplicationContext().getSharedPreferences("TypeUser", MODE_PRIVATE);
        String selectedUser = mPref.getString("user", "");
        Toast.makeText(this, "El rol seleccionado es " + selectedUser, Toast.LENGTH_SHORT).show();
        mProgressBar = new ProgressDialog(RegisterActivity.this);



    }

    public void verificarCredenciales(){
        String username = txtInputUsername.getText().toString();
        String email = txtInputEmail.getText().toString();
        String password = txtInputPassword.getText().toString();
        String confirmPass = txtInputConfirmPassword.getText().toString();
        if(username.isEmpty() || username.length() < 5){
            showError(txtInputUsername,"Username no valido");
        }else if (email.isEmpty() || !email.contains("@")){
            showError(txtInputEmail,"Email no valido");
        }else if (password.isEmpty() || password.length() < 7){
            showError(txtInputPassword,"Clave no valida minimo 7 caracteres");
        }else if (confirmPass.isEmpty() || !confirmPass.equals(password)){
            showError(txtInputConfirmPassword,"Clave no valida, no coincide.");
        }else{
            //Mostrar ProgressBar
            mProgressBar.setTitle("Proceso de Registro");
            mProgressBar.setMessage("Registrando usuario, espere un momento");
            mProgressBar.setCanceledOnTouchOutside(false);
            mProgressBar.show();
            //Registrar usuario
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                mProgressBar.dismiss();
                guardarUsuario(username,email);
                        //redireccionar - intent a login
                        Intent intent = new Intent(RegisterActivity.this, loginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Registrado Correctamente", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "No se puede registrar",Toast.LENGTH_LONG).show();
                    }
                }
            });
            //Exitoso -> Mostrar toast

            //ocultar progressBar

        }

    }

    private void showError(EditText input, String s){
        input.setError(s);
        input.requestFocus();
    }
    private void guardarUsuario(String name, String email) {
        mPref = getApplicationContext().getSharedPreferences("TypeUser", MODE_PRIVATE);
        String selectedUser = mPref.getString("user", "");
        Toast.makeText(this, "El rol seleccionado es " + selectedUser, Toast.LENGTH_SHORT).show();

        // Obtener UID del usuario actual
        String userId = mAuth.getCurrentUser().getUid();

        // Crear una nueva instancia de User con los valores correspondientes
        User user = new User(userId, name, email, selectedUser);

        if (selectedUser.equals("Client")) {
            mDatabase.child("Users").child("clients").child(userId).setValue(user);
        } else if (selectedUser.equals("Driver")) {
            mDatabase.child("Users").child("drivers").child(userId).setValue(user);
        } else if (selectedUser.equals("Proprietary")) {
            mDatabase.child("Users").child("proprietary").child(userId).setValue(user);
        }
    }




}