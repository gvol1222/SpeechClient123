package recogniton_service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.bill.Activities.R;

import activities.MainActivity;

/**
 * Created by bill on 11/30/17.
 */

public class ForeGroundRecognition extends SpeechService {

    private static final int NOTIFY_ID = 1;
    private final IBinder assistantBinder = new AssistantBinder();

    @Override
    public void StartInteract() {
        super.StartInteract();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("testNotify")
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText("testNotify");
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);

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
