package recogniton_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.bill.Activities.R;

import applications.Constatns;
import wit_connection.WitResponse;

/**
 * Created by bill on 11/14/17.
 */

public abstract class SpeechService extends RecognitionService  {

    public static final String BroadcastAction = "com.example.bill.Activities.MainActivity.UpdateGui";
    private final String TAG = this.getClass().getSimpleName();
    public static final String HAS_WIT = "hasWit";
    private String msg ="";
    private boolean hasWit;


    private Intent broadcastIntent;
    @Override
    public void onCreate() {
        super.onCreate();
        broadcastIntent = new Intent(BroadcastAction);
        SetReceivers();


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
        unregisterReceiver(MessageRes);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void OnSpeechError(int Error){
        broadcastIntent.putExtra("ripple", "ripple_stop");
        sendBroadcast(broadcastIntent);
        if (isActivated()) {
            //app.Stage=Constatns.NO_SPEACH_STAGE;
            Toast.makeText(this, "Η αναγνώριση τερματίζει", Toast.LENGTH_SHORT).show();
        }
        setActivated(false);
        //close recognition if not continuous
        CancelOnNotContinuous();
        //mute audio beep
        Mute(true);
        Constatns.app.Init();
        SendMessage("");

    }

    //speech listener methods
    @Override
    public void OnSpeechLiveResult(String LiveResult) {
        Log.i(TAG, "activated is " + isActivated() + " live result is " + LiveResult);
        //send the text from speech of user on main activity
        if (isActivated() ) {
            SendMessage(LiveResult);
        } else {
            SendMessage("");
        }
    }

    @Override
    public void OnSpeechResult(String Result) {
        Log.i(TAG, "activated is " + isActivated() + " final result is " + Result);



        if (isActivated() ) {
            new WitResponse(getApplicationContext()).execute(Result);
        } else if (Result.equals(getResources().getString(R.string.title_activity_gui))) {
            Mute(false);
            StartMessage(getApplicationContext().getResources().getString(R.string.StartMessage));
            setActivated(true);
        }
    }
    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "user ends speaking");
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void SetReceivers(){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter MessageFilter = new IntentFilter();
        //notification action
        MessageFilter.addAction(Constatns.NOT_ACTION);
        //maestro action
        MessageFilter.addAction(Constatns.MAESTRO_ACTION);
        localBroadcastManager.registerReceiver(MessageRes, MessageFilter);
        //maestro action
        registerReceiver(MessageRes,MessageFilter);

    }
    private void speak (String message,boolean recognize_after){
        //Intent msg = new Intent();

        if (recognize_after)
            setActivated(true);
        else
            setActivated(false);

        StartMessage(message);
    }
    //send message to activity
    protected void SendMessage(String msg) {
        Log.i(TAG, "message of sendmessage method  is   " + msg);
        broadcastIntent.putExtra("result", msg);
        sendBroadcast(broadcastIntent);
    }

    private final BroadcastReceiver MessageRes= new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action != null) {
                if (action.equals(Constatns.NOT_ACTION)){
                    Log.i(TAG, "notification action activate boolean  is " + isActivated());
                    if (isActivated()) {
                        StopSrecognition();
                        setActivated(false);
                    } else {
                        StartInteract();

                    }
                }else if(action.equals(Constatns.MAESTRO_ACTION)){

                    String speak =intent.getStringExtra("speak");
                    boolean reocgnizeAfter = intent.getBooleanExtra("rec",true);
                    String message = intent.getStringExtra("msg");
                    if (speak.equals("speak")){
                        speak(message,reocgnizeAfter);
                    }
                }
            }


        }
    };
}
