package co.edu.unipiloto.ingreso.Proprietary;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.ingreso.Oferta;
import co.edu.unipiloto.ingreso.R;

public class OfertaFragment extends Fragment {

    private Spinner spinnerDrivers;
    private Spinner spinnerTrucks;
    private Spinner spinnerOrigin;
    private Spinner spinnerDestination;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oferta, container, false);

        // Inicializar Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referenciar Spinners
        spinnerDrivers = view.findViewById(R.id.spinner_drivers);
        spinnerTrucks = view.findViewById(R.id.spinner_trucks);
        spinnerOrigin = view.findViewById(R.id.spinner_origin);
        spinnerDestination = view.findViewById(R.id.spinner_destination);
        spinnerDestination.setSelection(1);

        // Cargar datos de conductores
        loadDrivers();

        // Cargar datos de camiones
        loadTrucks();

        // Referenciar el botón de hacer envío
        Button btnDriver = view.findViewById(R.id.btn_driver);

        // Configurar el OnClickListener para el botón de hacer envío
        btnDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para guardar la oferta en la base de datos
                guardarOferta();
            }
        });

        return view;
    }

    private void loadDrivers() {
        mDatabase.child("Users").child("drivers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> driverNames = new ArrayList<>();
                for (DataSnapshot driverSnapshot : dataSnapshot.getChildren()) {
                    String name = driverSnapshot.child("name").getValue(String.class);
                    if (name != null) {
                        driverNames.add(name);
                    }
                }
                ArrayAdapter<String> driverAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, driverNames);
                driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDrivers.setAdapter(driverAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Error al cargar los conductores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTrucks() {
        mDatabase.child("camiones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> truckPlates = new ArrayList<>();
                for (DataSnapshot truckSnapshot : dataSnapshot.getChildren()) {
                    String plate = truckSnapshot.child("placa").getValue(String.class);
                    if (plate != null) {
                        truckPlates.add(plate);
                    }
                }
                ArrayAdapter<String> truckAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, truckPlates);
                truckAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTrucks.setAdapter(truckAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Error al cargar los camiones", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarOferta() {
        // Obtener los valores seleccionados en los spinners
        String conductorSeleccionado = spinnerDrivers.getSelectedItem().toString();
        String camionSeleccionado = spinnerTrucks.getSelectedItem().toString();
        String origenSeleccionado = spinnerOrigin.getSelectedItem().toString();
        String destinoSeleccionado = spinnerDestination.getSelectedItem().toString();

        // Crear una instancia de Oferta con los datos seleccionados
        Oferta oferta = new Oferta(conductorSeleccionado, camionSeleccionado, origenSeleccionado, destinoSeleccionado);

        // Guardar la oferta en la base de datos Firebase
        DatabaseReference ofertasRef = mDatabase.child("Ofertas").push();
        ofertasRef.setValue(oferta);

        // Mostrar un mensaje de éxito
        Toast.makeText(requireContext(), "Oferta guardada correctamente", Toast.LENGTH_SHORT).show();

        // Limpiar los spinners
        spinnerDrivers.setSelection(0);
        spinnerTrucks.setSelection(0);
        spinnerOrigin.setSelection(0);
        spinnerDestination.setSelection(1);
    }

}
