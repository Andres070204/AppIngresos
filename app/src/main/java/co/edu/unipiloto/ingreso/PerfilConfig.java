package co.edu.unipiloto.ingreso;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.edu.unipiloto.ingreso.R;

public class PerfilConfig extends Fragment {

    private ImageView imageProfile;
    private TextView textUsername;
    private TextView textEmail;
    private Button buttonEditUsername;
    private Button buttonEditEmail;
    private Button buttonChangePassword;
    private EditText editTextNewPassword;
    private EditText editTextCurrentPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referencias a los elementos de la interfaz
        imageProfile = view.findViewById(R.id.image_profile);


        buttonEditUsername = view.findViewById(R.id.button_edit_username); // Inicializar buttonEditUsername
        buttonEditEmail = view.findViewById(R.id.button_edit_email); // Inicializar buttonEditEmail
        buttonChangePassword = view.findViewById(R.id.button_change_password);
        editTextNewPassword = view.findViewById(R.id.edit_text_new_password);
        editTextCurrentPassword = view.findViewById(R.id.edit_text_current_password);

        // Obtener el usuario autenticado y cargar sus datos
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadUserData(currentUser.getUid());
        }

        // Configurar los listeners para los botones
        buttonEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para editar el nombre de usuario
                editUsername();
            }
        });

        buttonEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para editar el email
                editEmail();
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para cambiar la contraseña
                changePassword();
            }
        });

        return view;
    }

    private void loadUserData(String userId) {
        mDatabase.child("Users").child("clients").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    textUsername.setText(username);
                    textEmail.setText(email); // Asegúrate de que textEmail no sea nulo
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editUsername() {
        // Implementar la lógica para editar el nombre de usuario
        String newUsername = textUsername.getText().toString().trim();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mDatabase.child("Users").child("clients").child(currentUser.getUid()).child("name").setValue(newUsername);
            Toast.makeText(getContext(), "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();
        }
    }

    private void editEmail() {
        // Implementar la lógica para editar el email
        String newEmail = textEmail.getText().toString().trim();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mDatabase.child("Users").child("clients").child(currentUser.getUid()).child("email").setValue(newEmail);
                            Toast.makeText(getContext(), "Email actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar el email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void changePassword() {
        String currentPassword = editTextCurrentPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(getContext(), "Ingrese la contraseña actual", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(getContext(), "Ingrese la nueva contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(getContext(), "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
