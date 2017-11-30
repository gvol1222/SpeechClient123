package recogniton_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.bill.Activities.R;

import applications.AppIntentService;
import wit_connection.WitResponse;
import wit_connection.WitResponseMessage;

/**
 * Created by bill on 11/14/17.
 */

public abstract class SpeechService extends ServiceHelper implements WitResponseMessage {

    public static final String BroadcastAction = "com.example.bill.Activities.MainActivity.UpdateGui";
    private static final String TAG = "BtroadCast";
    private Intent broadcastIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        broadcastIntent = new Intent(BroadcastAction);
        IntentFilter broadcastFilter = new IntentFilter(ResponseReceiver.LOCAL_ACTION);
        ResponseReceiver receiver = new ResponseReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver, broadcastFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    //speech listener methods
    @Override
    public void OnSpeechLiveResult(String LiveResult) {

        if (isActivated()) {
            SendMessage(LiveResult);
        } else {
            SendMessage("");
        }
    }

    @Override
    public void OnSpeechResult(String Result) {
        if (isActivated()) {
            wit_connection.WitResponse witResponse = new WitResponse(this);
            witResponse.execute(Result);
        } else if (Result.equals("Γιάννη")) {
            StartMessage(getApplicationContext().getResources().getString(R.string.StartMessage));
            setActivated(true);


        }
    }

    @Override
    public void OnSpeechError(int Error) {
        super.OnSpeechError(Error);
    }

    //wit response methods
    @Override
    public void ErrorOnCommand(String msg) {
        if (isActivated()) {
            StartMessage(msg);
        }
    }

    @Override
    public void ErrorCommand(String msg) {
        if (isActivated()) {
            StartMessage(msg);
        }
    }

    @Override
    public void Message(String search, String application, String conf) {
        SendMessage("");
        Intent newint = new Intent(this, AppIntentService.class);
        newint.putExtra(AppIntentService.APP_KIND, application);
        newint.putExtra(AppIntentService.QUERY, search);
        startService(newint);
    }

    //send message to activity
    private void SendMessage(String msg) {
        broadcastIntent.putExtra("result", msg);
        sendBroadcast(broadcastIntent);
    }

    //create receiver for run app
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String LOCAL_ACTION =
                "com.example.bill.speechclient.applications.appintentservice.COMMAND_DONE";

        @Override
        public void onReceive(Context context, Intent intent) {
            String appResp = intent.getStringExtra(AppIntentService.RESULT);
            StartMessage(appResp);
            CancelOnNotContinous();
            setActivated(false);
        }
    }


}
