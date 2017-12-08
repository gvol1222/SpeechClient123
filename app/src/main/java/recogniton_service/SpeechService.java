package recogniton_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.bill.Activities.R;

import applications.AppIntentService;
import applications.CallTel;
import wit_connection.WitResponse;
import wit_connection.WitResponseMessage;

/**
 * Created by bill on 11/14/17.
 */

public abstract class SpeechService extends ServiceHelper implements WitResponseMessage {

    public static final String BroadcastAction = "com.example.bill.Activities.MainActivity.UpdateGui";
    private final String TAG = this.getClass().getSimpleName();
    private final BroadcastReceiver NotAction = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "activated is " + isActivated());
            if (isActivated()) {
                StopSrecognition();
                setActivated(false);
            } else {
                StartInteract();

            }
        }
    };
    boolean isinteractive = false;
    private Intent broadcastIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastIntent = new Intent(BroadcastAction);
        IntentFilter broadcastFilter = new IntentFilter(ResponseReceiver.LOCAL_ACTION);
        ResponseReceiver receiver = new ResponseReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver, broadcastFilter);
        registerReceiver(NotAction, new IntentFilter("notification.action"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "unregisterReceiver ");
        unregisterReceiver(NotAction);

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
        Log.i(TAG, "activated is " + isActivated() + " live result is " + LiveResult);
        if (isActivated() && !isinteractive) {
            SendMessage(LiveResult);
        } else {
            SendMessage("");
        }
    }

    @Override
    public void OnSpeechResult(String Result) {
        Log.i(TAG, "activated is " + isActivated() + " final result is " + Result);

        if (isinteractive) {
            if (Result.equals("ναι")) {
                CallTel.newCall(AppIntentService.tel, this);
            } else {
                StartMessage("όπως επιθυμείτε.");
            }
        }

        if (isActivated() && !isinteractive) {
            wit_connection.WitResponse witResponse = new WitResponse(this);
            witResponse.execute(Result);
        } else if (Result.equals("Ίριδα")) {
            StartMessage(getApplicationContext().getResources().getString(R.string.StartMessage));
            setActivated(true);

        }
    }

    @Override
    public void OnSpeechError(int Error) {
        super.OnSpeechError(Error);
        isinteractive = false;

    }

    //wit response methods
    @Override
    public void ErrorOnCommand(String msg) {
        Log.i(TAG, "Error on command message is " + msg);
        if (!isinteractive && isActivated()) {
            SendMessage("");
            StartMessage(msg);
        }
    }

    @Override
    public void ErrorCommand(String msg) {
        Log.i(TAG, "Error command message is " + msg);
        if (!isinteractive && isActivated()) {
            SendMessage("");
            StartMessage(msg);
        }
    }

    @Override
    public void Message(String search, String application, String conf) {
        Log.i(TAG, "Search parameter is  " + search + " application kind is" + application);
        SendMessage("");
        if (!isinteractive) {
            Intent newint = new Intent(this, AppIntentService.class);
            newint.putExtra(AppIntentService.APP_KIND, application);
            newint.putExtra(AppIntentService.QUERY, search);
            startService(newint);
        }
    }


    //send message to activity
    protected void SendMessage(String msg) {
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
            if (!appResp.equals(context.getResources().getString(R.string.make_call_error_message))) {
                StartMessage("Επιθυμείτε να πραγματοποιηθεί το τηλεφώνημα ναί ή όχι");
                isinteractive = true;
                setFirst(true);
            } else if (appResp.equals(context.getResources().getString(R.string.make_call_error_message))) {
                StartMessage(appResp);
                isinteractive = false;
                setActivated(false);
            } else {
                StartMessage(appResp);
                isinteractive = false;
                setActivated(false);

            }
            Log.i(TAG, "Response from command is " + appResp);



        }
    }


}
