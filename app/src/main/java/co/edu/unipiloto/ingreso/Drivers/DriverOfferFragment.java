package co.edu.unipiloto.ingreso.Drivers;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import co.edu.unipiloto.ingreso.Oferta;
import co.edu.unipiloto.ingreso.R;


public class DriverOfferFragment extends Fragment {

    private ListView listViewOffers;
    private TextView textViewTitle;
    private Button btnAccept, btnReject;

    private DatabaseReference mDatabase;
    private String driverName;

    private List<Oferta> offerList;
    private ArrayAdapter<Oferta> offerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oferta_driver, container, false);


        mDatabase = FirebaseDatabase.getInstance().getReference();


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            driverName = currentUser.getDisplayName();
        }




        listViewOffers = view.findViewById(R.id.list_view_offers);
        textViewTitle = view.findViewById(R.id.text_view_title);
        btnAccept = view.findViewById(R.id.btn_accept);
        btnReject = view.findViewById(R.id.btn_reject);


        offerList = new ArrayList<>();
        offerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, offerList);
        listViewOffers.setAdapter(offerAdapter);


        listViewOffers.setOnItemClickListener((parent, view1, position, id) -> {
        });


        loadAssignedOffers();

        return view;
    }

    private void loadAssignedOffers() {
        mDatabase.child("Ofertas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                offerList.clear();
                for (DataSnapshot offerSnapshot : dataSnapshot.getChildren()) {
                    Oferta oferta = offerSnapshot.getValue(Oferta.class);
                    if (oferta != null && oferta.getConductor().equals(driverName)) {
                        offerList.add(oferta);
                    }
                }

                offerAdapter.notifyDataSetChanged();


                if (offerList.isEmpty()) {
                    textViewTitle.setVisibility(View.VISIBLE);
                    btnAccept.setVisibility(View.GONE);
                    btnReject.setVisibility(View.GONE);
                } else {
                    textViewTitle.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Error al cargar las ofertas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}