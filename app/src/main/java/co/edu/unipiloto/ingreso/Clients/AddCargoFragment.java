package co.edu.unipiloto.ingreso.Clients;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class AddCargoFragment extends Fragment {

    private EditText editTextDescription, editTextWeight, editTextVolume, editTextDestination, editTextOrigin;
    private Spinner spinnerDriver, spinnerTruck;
    private Button buttonSubmit;
    private DatabaseReference mDatabase;

    private List<String> driverIds = new ArrayList<>();
    private List<String> truckIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_carga, container, false);

        // Inicializar Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referenciar vistas
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextWeight = view.findViewById(R.id.editTextWeight);
        editTextVolume = view.findViewById(R.id.editTextVolume);
        editTextDestination = view.findViewById(R.id.editTextDestination);
        editTextOrigin = view.findViewById(R.id.editTextOrigin);
        spinnerDriver = view.findViewById(R.id.spinnerDriver);
        spinnerTruck = view.findViewById(R.id.spinnerTruck);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);

        // Cargar datos de conductores y camiones
        loadDrivers();
        loadTrucks();

        // Configurar el evento clic para el botón
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCargoToFirebase();
            }
        });

        return view;
    }

    private void loadDrivers() {
        mDatabase.child("Users").child("drivers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> driverNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String driverId = snapshot.getKey();
                    String driverName = snapshot.child("name").getValue(String.class);
                    driverIds.add(driverId);
                    driverNames.add(driverName);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, driverNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDriver.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
            }
        });
    }

    private void loadTrucks() {
        mDatabase.child("camiones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> truckPlates = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String truckId = snapshot.getKey();
                    String truckPlate = snapshot.child("placa").getValue(String.class);
                    truckIds.add(truckId);
                    truckPlates.add(truckPlate);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, truckPlates);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTruck.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
            }
        });
    }
    private void addCargoToFirebase() {
        // Obtener los valores ingresados por el usuario
        String description = editTextDescription.getText().toString().trim();
        String weight = editTextWeight.getText().toString().trim();
        String volume = editTextVolume.getText().toString().trim();
        String origin = editTextOrigin.getText().toString().trim();
        String destination = editTextDestination.getText().toString().trim();

        String driverId = driverIds.get(spinnerDriver.getSelectedItemPosition());
        String truckId = truckIds.get(spinnerTruck.getSelectedItemPosition());

        // Validar que los campos no estén vacíos
        if (description.isEmpty() || weight.isEmpty() || volume.isEmpty() || destination.isEmpty()) {
            Toast.makeText(getContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el nombre del usuario autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();

            // Crear un nuevo objeto de carga
            Cargo cargo = new Cargo(userName, description, Double.parseDouble(weight), Double.parseDouble(volume),origin, destination, driverId, truckId);

            // Guardar el objeto de carga en la base de datos Firebase
            DatabaseReference cargoRef = mDatabase.child("Cargas").push();
            cargoRef.setValue(cargo);

            // Mostrar un mensaje de éxito
            Toast.makeText(getContext(), "Carga añadida correctamente", Toast.LENGTH_SHORT).show();

            // Limpiar los campos después de agregar la carga
            editTextDescription.setText("");
            editTextWeight.setText("");
            editTextVolume.setText("");
            editTextOrigin.setText("");
            editTextDestination.setText("");
            spinnerDriver.setSelection(0);
            spinnerTruck.setSelection(0);
        } else {
            // Manejar el caso en el que el usuario no está autenticado
            Toast.makeText(getContext(), "No se pudo obtener el usuario autenticado", Toast.LENGTH_SHORT).show();
        }
    }
}
