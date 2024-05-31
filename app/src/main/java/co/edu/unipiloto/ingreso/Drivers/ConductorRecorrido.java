package co.edu.unipiloto.ingreso.Drivers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;


import android.Manifest;

import co.edu.unipiloto.ingreso.R;

public class ConductorRecorrido extends Fragment {

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Button buttonReportLocation;
    private Button buttonSeguirRuta;
    private TextView textViewLocationHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recorrido_conductor, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        buttonReportLocation = view.findViewById(R.id.buttonReportLocation);
        buttonSeguirRuta = view.findViewById(R.id.buttonSeguirRuta);
        textViewLocationHistory = view.findViewById(R.id.textViewLocationHistory);

        buttonReportLocation.setOnClickListener(v -> reportCurrentLocation());
        buttonSeguirRuta.setOnClickListener(v -> getOriginAndDestination());



        return view;
    }

    private void getOriginAndDestination() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String driverId = currentUser.getUid();
            Log.d("DriverID", driverId); // Verificar el ID del conductor en el registro

            DatabaseReference cargaRef = mDatabase.child("Cargas");
            cargaRef.orderByChild("driverId").equalTo(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot cargaSnapshot : dataSnapshot.getChildren()) {
                            String origin = cargaSnapshot.child("origin").getValue(String.class);
                            String destination = cargaSnapshot.child("destination").getValue(String.class);
                            if (origin != null && destination != null) {
                                openGoogleMaps(origin, destination);
                                return; // Detener el bucle después de encontrar la carga
                            }
                        }
                        // Si no se encuentra ninguna carga con los datos de origen y destino
                        Toast.makeText(getContext(), "No se encontró la información de origen y destino", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "No se encontraron datos de carga para este conductor", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error al obtener los datos de origen y destino", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }



    private void reportCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }


        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    getAddressAndSaveLocation(location);
                } else {
                    Toast.makeText(getContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getAddressAndSaveLocation(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getLocality() + ", " + address.getSubLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName();
                saveLocationToDatabase(location, addressText);
            } else {
                Toast.makeText(getContext(), "No se pudo obtener la dirección", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al obtener la dirección", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLocationToDatabase(Location location, String addressText) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Por favor inicie sesión para reportar ubicación", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference userRef = mDatabase.child("Users").child("drivers").child(userId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        String driverName = snapshot.child("name").getValue(String.class);

                        String locationId = mDatabase.child("LocationReports").child(userId).push().getKey();
                        if (locationId != null) {
                            DatabaseReference locationRef = mDatabase.child("LocationReports").child(userId).child(locationId);

                            LocationReport locationReport = new LocationReport(location.getLatitude(), location.getLongitude(), System.currentTimeMillis(), addressText, driverName);
                            locationRef.setValue(locationReport);

                            textViewLocationHistory.append("\n" + locationReport.toString());

                            Toast.makeText(getContext(), "Ubicación reportada correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "No se pudo generar ID para el reporte de ubicación", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No se encontró la información del conductor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al obtener la información del conductor", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGoogleMaps(String origin, String destination) {
        try {
            // Codificar los valores de origen y destino para asegurarse de que sean válidos en una URL
            String encodedOrigin = URLEncoder.encode(origin, "UTF-8");
            String encodedDestination = URLEncoder.encode(destination, "UTF-8");

            // Construir la URL de Google Maps con los valores codificados
            Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + encodedDestination + "&destination=" + encodedOrigin);

            // Crear el intent para abrir Google Maps
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            // Verificar que haya una aplicación que pueda manejar el intent
            if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(requireContext(), "Google Maps no está instalado", Toast.LENGTH_SHORT).show();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al codificar la URL para Google Maps", Toast.LENGTH_SHORT).show();
        }
    }


    private static class LocationReport {
        public double latitude;
        public double longitude;
        public long timestamp;
        public String address;
        public String driverName;

        public LocationReport() {
        }

        public LocationReport(double latitude, double longitude, long timestamp, String address, String driverName) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
            this.address = address;
            this.driverName = driverName;
        }

        @Override
        public String toString() {
            return "Conductor: " + driverName  + "\n Ubicacion: " + address ;
        }
    }
}

