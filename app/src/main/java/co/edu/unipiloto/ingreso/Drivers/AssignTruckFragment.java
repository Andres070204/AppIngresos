package co.edu.unipiloto.ingreso.Drivers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.ingreso.R;

public class AssignTruckFragment extends Fragment {

    private Spinner spinnerTruck;
    private Button buttonAssignTruck;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser ;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.escoger_camion, container, false);

        // Inicializar la referencia de la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Referenciar vistas
        spinnerTruck = view.findViewById(R.id.spinnerTruck);
        buttonAssignTruck = view.findViewById(R.id.buttonAssignTruck);

        // Configurar el Spinner con la lista de camiones disponibles
        setupTruckSpinner();

        // Configurar el evento de clic del botón de asignar camión
        buttonAssignTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignDriverToTruck();
            }
        });

        return view;
    }

    private void setupTruckSpinner() {
        // Obtener la referencia de la base de datos de camiones
        DatabaseReference trucksRef = mDatabase.child("camiones");

        // Leer los camiones disponibles de la base de datos
        trucksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> truckList = new ArrayList<>();
                for (DataSnapshot truckSnapshot : dataSnapshot.getChildren()) {
                    // Obtener el nombre de cada camión y agregarlo a la lista
                    String truckName = truckSnapshot.child("placa").getValue(String.class);
                    truckList.add(truckName);
                }

                // Configurar el adaptador para el Spinner
                ArrayAdapter<String> truckAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, truckList);
                truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTruck.setAdapter(truckAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
                Toast.makeText(requireContext(), "Error al leer los camiones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignDriverToTruck() {
        // Obtener el ID del camión seleccionado en el Spinner
        String selectedTruckName = spinnerTruck.getSelectedItem().toString();

        // Obtener la referencia del nodo de camiones para buscar el camión por su nombre de placa
        DatabaseReference trucksRef = mDatabase.child("camiones");

        // Buscar el camión por su nombre de placa
        trucksRef.orderByChild("placa").equalTo(selectedTruckName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Obtener el ID del camión
                    String truckId = dataSnapshot.getChildren().iterator().next().getKey();

                    // Obtener el ID del conductor actualmente autenticado
                    String driverId = mAuth.getCurrentUser().getUid();

                    // Actualizar el campo truckId del conductor con el ID del camión seleccionado
                    mDatabase.child("Users").child("drivers").child(driverId).child("truckId").setValue(truckId)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // La asignación del camión al conductor se realizó con éxito
                                    Toast.makeText(requireContext(), "Camión asignado correctamente al conductor", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Ocurrió un error al intentar asignar el camión al conductor
                                    Toast.makeText(requireContext(), "Error al asignar el camión al conductor", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // El camión seleccionado no se encontró en la base de datos
                    Toast.makeText(requireContext(), "El camión seleccionado no se encontró", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
                Toast.makeText(requireContext(), "Error al obtener el camión seleccionado", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
