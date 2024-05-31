package co.edu.unipiloto.ingreso;

/*import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapaGoogle extends AppCompatActivity implements OnMapReadyCallback{
    private SupportMapFragment fragmentMap;
    private GoogleMap maps;
    private LocationRequest ubicacion;
    private FusedLocationProviderClient fused;

    private final static int LOCATIONREQUESTCALL = 1;
    LocationCallback locationcallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult){
            for (Location location: locationResult.getLocations()){
                if (getApplicationContext()!= null){
                    maps.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fused = LocationServices.getFusedLocationProviderClient(this);
        //fragmentMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Mapa);
        fragmentMap.getMapAsync((OnMapReadyCallback) this);

    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        maps = googleMap;
        maps.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        maps.getUiSettings().setZoomControlsEnabled(true);

        ubicacion = new LocationRequest();
        ubicacion.setInterval(1000);
        ubicacion.setFastestInterval(1000);
        ubicacion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        ubicacion.setSmallestDisplacement(5);
        startLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATIONREQUESTCALL ){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    fused.requestLocationUpdates(ubicacion , locationcallback , Looper.myLooper());
                }
            }
        }
    }

    private void startLocation(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                fused.requestLocationUpdates(ubicacion , locationcallback , Looper.myLooper());
            }else{
                checkLocationPermissions();
            }

        }else {
            fused.requestLocationUpdates(ubicacion , locationcallback , Looper.myLooper());
        }

    }
    private void checkLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Dale acceso a los permisos")
                        .setMessage("Esta aplicación requiere permisos de ubicación")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapaGoogle.this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATIONREQUESTCALL );
                            }

                        })
                        .create()
                        .show();

            }else{
                ActivityCompat.requestPermissions(MapaGoogle.this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATIONREQUESTCALL );
            }
        }
    }
}
*/