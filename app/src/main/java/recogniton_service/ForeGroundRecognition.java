package recogniton_service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.bill.Activities.R;

/**
 * Created by bill on 11/30/17.
 */

public class ForeGroundRecognition extends SpeechService {

    private static final int NOTIFY_ID = 1;
    private final IBinder assistantBinder = new AssistantBinder();
    Notification not;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent ConIntent = new Intent("notification.action");
        PendingIntent ActionIntent = PendingIntent.getBroadcast(this, 4, ConIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        CreateNotification(ActionIntent);

        return Service.START_STICKY;
    }


    private void CreateNotification(PendingIntent ActionIntent) {
        Notification.Builder builder = new Notification.Builder(this);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_name);
        builder.setContentIntent(ActionIntent)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(bitmap)
                .setTicker("Η προσωπική σας βοηθός")
                .setOngoing(true)
                .setContentTitle("Ίριδα")
                .setContentText("Η προσωπική σας βοηθός");
        not = builder.build();
        startForeground(NOTIFY_ID,
                not);

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return assistantBinder;
    }

    public class AssistantBinder extends Binder {
        public ForeGroundRecognition getService() {
            return ForeGroundRecognition.this;
        }
    }
}
