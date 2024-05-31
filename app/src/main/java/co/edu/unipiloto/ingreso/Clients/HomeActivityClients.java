package co.edu.unipiloto.ingreso.Clients;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import co.edu.unipiloto.ingreso.PerfilConfig;
import co.edu.unipiloto.ingreso.R;
import co.edu.unipiloto.ingreso.databinding.ActivityMainBinding;
import co.edu.unipiloto.ingreso.servicios.CalificacionFragment;

public class HomeActivityClients extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new AddCargoFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.homes) {
                replaceFragment(new AddCargoFragment());
            } else if (item.getItemId() == R.id.shorts) {
                replaceFragment(new Recorrido());
            } else if (item.getItemId() == R.id.pedidos) {
                replaceFragment(new PerfilConfig());
            } else if (item.getItemId() == R.id.config) {
                replaceFragment(new CalificacionFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
