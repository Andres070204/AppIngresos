package co.edu.unipiloto.ingreso.Proprietary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.ingreso.R;

public class CamionRegistroFragment extends Fragment {

    private EditText editTextPlaca, editTextModelo, editTextColor, editTextMotor;
    private Button btnGuardar, btnMostrarCamiones;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference camionesRef;
    private List<Camion> camionesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar la vista para este fragmento
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar Firebase
        FirebaseApp.initializeApp(getContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("camiones");
        camionesRef = database.getReference("camiones");
        camionesList = new ArrayList<>();

        // Referencias a los elementos de la interfaz
        editTextPlaca = view.findViewById(R.id.editTextPlaca);
        editTextModelo = view.findViewById(R.id.editTextModelo);
        editTextColor = view.findViewById(R.id.editTextColor);
        editTextMotor = view.findViewById(R.id.editTextMotor);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnMostrarCamiones = view.findViewById(R.id.btnMostrarCamiones);

        // Evento del botón Guardar
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String placa = editTextPlaca.getText().toString();
                String modelo = editTextModelo.getText().toString();
                String color = editTextColor.getText().toString();
                String motor = editTextMotor.getText().toString();

                // Obtener el ID del propietario (supongamos que ya lo tienes disponible)
                String propietarioId = currentUser.getUid();// Reemplaza "ID_del_propietario" con el ID real del propietario

                // Validar que los campos no estén vacíos y que la placa tenga 6 caracteres
                if (placa.isEmpty() || modelo.isEmpty() || color.isEmpty() || motor.isEmpty()) {
                    Toast.makeText(getActivity(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                } else if (placa.length() != 6) {
                    Toast.makeText(getActivity(), "La placa debe contener 6 caracteres", Toast.LENGTH_SHORT).show();
                } else {
                    // Consultar la base de datos para verificar si la placa ya está registrada
                    myRef.orderByChild("placa").equalTo(placa).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Si la placa ya existe, mostrar un mensaje
                                Toast.makeText(getActivity(), "Camión ya registrado", Toast.LENGTH_SHORT).show();
                            } else {
                                // Si la placa no existe, crear una nueva instancia de Camion y guardarla
                                Camion camion = new Camion(placa, modelo, color, motor, propietarioId); // Pasar el ID del propietario
                                myRef.push().setValue(camion).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Mostrar mensaje de éxito y limpiar los campos
                                            Toast.makeText(getActivity(), "Camión registrado correctamente", Toast.LENGTH_SHORT).show();
                                            editTextPlaca.setText("");
                                            editTextModelo.setText("");
                                            editTextColor.setText("");
                                            editTextMotor.setText("");
                                        } else {
                                            // Mostrar mensaje de error si lo hay
                                            Toast.makeText(getActivity(), "Error al registrar el camión", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Manejar error de cancelación
                            Toast.makeText(getActivity(), "Error en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // Configurar el OnClickListener para el botón Mostrar Camiones
        btnMostrarCamiones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamar al método para mostrar los camiones del propietario
                mostrarCamionesDelPropietario();
            }
        });

        return view;
    }

    private void mostrarCamionesDelPropietario() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String propietarioId = currentUser.getUid();

            Query query = camionesRef.orderByChild("propietarioId").equalTo(propietarioId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    camionesList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Camion camion = snapshot.getValue(Camion.class);
                        if (camion != null) {
                            camionesList.add(camion);
                        }
                    }

                    // Aquí debes implementar la lógica para mostrar los camiones en otro fragmento o actividad
                    // Por ejemplo, podrías enviar la lista de camiones a otro fragmento mediante un Bundle
                    // o iniciar una nueva actividad y pasar la lista de camiones como argumento
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error al cargar los camiones del propietario", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
