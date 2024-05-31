package co.edu.unipiloto.ingreso.servicios;

/*import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
public class Notificacion extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getNotification() != null) {
            Log.d("RecibiendoMessage", "Title de la noti: " + message.getNotification().getTitle());
            Log.d("RecibiendoMessage", "Body de la noti: " + message.getNotification().getBody());
        }
        if (message.getData().size() > 0) {
            Log.d("RecibiendoMessage", "Title en data: " + message.getData().get("title"));
            Log.d("RecibiendoMessage", "Body en data: " + message.getData().get("body"));
            Log.d("RecibiendoMessage", "BigText en data: " + message.getData().get("bigText"));
            Log.d("RecibiendoMessage", "Type en data: " + message.getData().get("type"));
            Log.d("RecibiendoMessage", "idCarga en data: " + message.getData().get("idCarga"));
        }
        String title = message.getNotification().getTitle();
        String content = message.getNotification().getBody();
        String bigText = content;



    }
}*/