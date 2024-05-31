package co.edu.unipiloto.ingreso.Clients;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.edu.unipiloto.ingreso.R;
import co.edu.unipiloto.ingreso.Transporte;

public class Recorrido extends Fragment {
    private EditText editTextOrigin, editTextDestination;
    private Button buttonSubmit, buttonShowRoutes, buttonShowDriverLocation;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recorrido, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Referenciar vistas
        editTextOrigin = view.findViewById(R.id.editTextOrigin);
        editTextDestination = view.findViewById(R.id.editTextDestination);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonShowRoutes = view.findViewById(R.id.buttonShowRoutes);
        buttonShowDriverLocation = view.findViewById(R.id.buttonShowDriverLocation);

        // Configurar el evento clic para el botón de guardar solicitud
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransportRequest();
            }
        });

        // Configurar el evento clic para el botón de mostrar rutas
        buttonShowRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoutesInMap();
            }
        });

        // Configurar el evento clic para el botón de mostrar la ubicación del conductor
        buttonShowDriverLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDriverLocationInMap();
            }
        });

        return view;
    }

    private void saveTransportRequest() {
        // Obtener los valores ingresados por el usuario
        String origin = editTextOrigin.getText().toString().trim();
        String destination = editTextDestination.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (origin.isEmpty() || destination.isEmpty()) {
            Toast.makeText(getContext(), "Por favor complete ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el usuario autenticado actual
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Obtener el nombre del usuario desde la base de datos
            DatabaseReference userRef = mDatabase.child("Users").child("clients").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userName = snapshot.child("name").getValue(String.class);

                        // Crear un nuevo objeto de solicitud de transporte
                        Transporte request = new Transporte(userName, origin, destination);

                        // Guardar el objeto de solicitud en la base de datos Firebase
                        DatabaseReference requestRef = mDatabase.child("TransportRequests").push();
                        requestRef.setValue(request);

                        // Mostrar un mensaje de éxito
                        Toast.makeText(getContext(), "Solicitud guardada correctamente", Toast.LENGTH_SHORT).show();

                        // Limpiar los campos después de guardar la solicitud
                        editTextOrigin.setText("");
                        editTextDestination.setText("");
                    } else {
                        Toast.makeText(getContext(), "No se pudo obtener la información del usuario", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Manejar posibles errores
                    Toast.makeText(getContext(), "Error al obtener la información del usuario", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Manejar el caso en el que el usuario no está autenticado
            Toast.makeText(getContext(), "No se pudo obtener el usuario autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void showRoutesInMap() {
        // Obtener los valores ingresados por el usuario
        String origin = editTextOrigin.getText().toString().trim();
        String destination = editTextDestination.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (origin.isEmpty() || destination.isEmpty()) {
            Toast.makeText(getContext(), "Por favor complete ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un intent para mostrar rutas en Google Maps
        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + origin + "&destination=" + destination + "&travelmode=driving");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Verificar que haya una aplicación que pueda manejar el intent
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getContext(), "Google Maps no está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDriverLocationInMap() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String driverId = currentUser.getUid();

            // Obtener la ubicación del conductor desde la base de datos
            DatabaseReference driverLocationRef = mDatabase.child("LocationReports").child(driverId);
            driverLocationRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                            Double latitude = locationSnapshot.child("latitude").getValue(Double.class);
                            Double longitude = locationSnapshot.child("longitude").getValue(Double.class);
                            String address = locationSnapshot.child("address").getValue(String.class);

                            if (latitude != null && longitude != null && address != null) {
                                // Construir el URI sin usar Uri.encode
                                String uri = "geo:" + latitude + "," + longitude + "?q=" + address;
                                Uri gmmIntentUri = Uri.parse(uri);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");

                                // Verificar que haya una aplicación que pueda manejar el intent
                                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                } else {
                                    Toast.makeText(getContext(), "Google Maps no está instalado", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Datos de ubicación no válidos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "No se encontró la ubicación del conductor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error al obtener la ubicación del conductor", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }





}
