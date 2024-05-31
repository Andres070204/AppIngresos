package co.edu.unipiloto.ingreso.Proprietary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.ingreso.R;

public class ListaCamionesFragment extends Fragment {

    private ListView listViewCamiones;
    private ArrayAdapter<String> adapter;
    private List<String> camionesList;
    private DatabaseReference camionesRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hola, container, false);

        listViewCamiones = view.findViewById(R.id.listViewCamiones);
        camionesList = new ArrayList<>();

        // Obtener referencia a la base de datos "camiones"
        camionesRef = FirebaseDatabase.getInstance().getReference().child("camiones");

        // Agregar un listener para obtener los datos de los camiones
        camionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Camion camion = snapshot.getValue(Camion.class);
                    if (camion != null) {
                        String infoCamion =  camion.getPlaca() + " " +
                                camion.getModelo() + " " +
                                camion.getColor();

                        camionesList.add(infoCamion);
                    }
                }
                adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos cambiaron
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
            }
        });

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, camionesList);
        listViewCamiones.setAdapter(adapter);

        return view;
    }
}