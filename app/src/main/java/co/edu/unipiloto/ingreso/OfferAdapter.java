package co.edu.unipiloto.ingreso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class OfferAdapter extends ArrayAdapter<Oferta> {

    private Context mContext;
    private List<Oferta> mOfferList;

    public OfferAdapter(@NonNull Context context, List<Oferta> offerList) {
        super(context, 0, offerList);
        mContext = context;
        mOfferList = offerList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.offer_list_item, parent, false);
        }

        final Oferta currentOffer = mOfferList.get(position);

        TextView conductorTextView = listItem.findViewById(R.id.text_conductor);
        conductorTextView.setText("Conductor: " + currentOffer.getConductor());

        TextView camionTextView = listItem.findViewById(R.id.text_camion);
        camionTextView.setText("Camión: " + currentOffer.getCamion());

        TextView origenTextView = listItem.findViewById(R.id.text_origen);
        origenTextView.setText("Origen: " + currentOffer.getOrigen());

        TextView destinoTextView = listItem.findViewById(R.id.text_destino);
        destinoTextView.setText("Destino: " + currentOffer.getDestino());

        Button acceptButton = listItem.findViewById(R.id.btn_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para aceptar la oferta
                Toast.makeText(mContext, "Oferta aceptada", Toast.LENGTH_SHORT).show();
            }
        });

        Button rejectButton = listItem.findViewById(R.id.btn_reject);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para rechazar la oferta
                Toast.makeText(mContext, "Oferta rechazada", Toast.LENGTH_SHORT).show();
            }
        });

        return listItem;
    }
}