package co.edu.unipiloto.ingreso.servicios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
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

import co.edu.unipiloto.ingreso.R;

public class CalificacionFragment extends Fragment {

    private RatingBar ratingBar;
    private EditText etComments;
    private Button btnSubmitRating;
    private TextView tvRatingDisplay;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calificacion, container, false);

        // Inicializar Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Referenciar elementos del layout
        ratingBar = view.findViewById(R.id.ratingBar);
        etComments = view.findViewById(R.id.etComments);
        btnSubmitRating = view.findViewById(R.id.btnSubmitRating);
        tvRatingDisplay = view.findViewById(R.id.tvRatingDisplay);

        // Manejar clic en el botón de enviar calificación
        btnSubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la calificación y los comentarios
                float rating = ratingBar.getRating();
                String comments = etComments.getText().toString();

                // Obtener el nombre del usuario actual
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userName = currentUser.getDisplayName();
                    // Guardar los datos en Firebase
                    saveRatingToFirebase(userName, rating, comments);
                } else {
                    // El usuario no está autenticado, manejar esto según el flujo de tu aplicación
                    Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Escuchar cambios en los datos de Firebase para mostrar los comentarios
        mDatabase.child("ratings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recuperar las calificaciones y comentarios guardados
                StringBuilder ratingsText = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Rating rating = snapshot.getValue(Rating.class);
                    if (rating != null) {
                        // Mostrar la calificación y los comentarios
                        ratingsText.append("Calificación: ").append(rating.rating).append("\nComentarios: ").append(rating.comments).append("\n\n");
                    }
                }
                // Mostrar todas las calificaciones y comentarios
                tvRatingDisplay.setText(ratingsText.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de Firebase
                Toast.makeText(getContext(), "Error al cargar las calificaciones", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void saveRatingToFirebase(String userName, float rating, String comments) {
        // Crear un nuevo nodo en la base de datos de Firebase para almacenar la calificación, los comentarios y el nombre del usuario
        String ratingId = mDatabase.child("ratings").push().getKey();
        Rating ratingObj = new Rating(userName, rating, comments);
        mDatabase.child("ratings").child(ratingId).setValue(ratingObj);

        // Mostrar mensaje de éxito
        Toast.makeText(getContext(), "Calificación enviada exitosamente", Toast.LENGTH_SHORT).show();
        etComments.setText("");
    }

    // Clase para representar la calificación y los comentarios
    public static class Rating {
        public String userName;
        public float rating;
        public String comments;

        public Rating() {
            // Constructor vacío requerido para Firebase
        }

        public Rating(String userName, float rating, String comments) {
            this.userName = userName;
            this.rating = rating;
            this.comments = comments;
        }
    }
}
