package recogniton_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.bill.Activities.R;

import applications.AppIntentService;
import applications.CallTel;

/**
 * Created by bill on 12/12/17.
 */

public abstract class Interact extends SpeechService {

    private final String TAG = this.getClass().getSimpleName();

    private String tel;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter broadcastFilter = new IntentFilter(ResponseReceiver.LOCAL_ACTION);
        ResponseReceiver receiver = new ResponseReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver, broadcastFilter);
    }


    @Override
    public void OnSpeechResult(String Result) {
        super.OnSpeechResult(Result);

        if (isIsinteractive()) {
            setIsinteractive(false);
            if (Result.equals("ναι")) {
                CallTel.newCall(tel, this);
            } else {
                StartMessage("όπως επιθυμείτε.");
            }
        }

    }

    @Override
    public void onEndOfSpeech() {
        super.onEndOfSpeech();

    }

    //create receiver for run app
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String LOCAL_ACTION =
                "com.example.bill.speechclient.applications.appintentservice.COMMAND_DONE";

        @Override
        public void onReceive(Context context, Intent intent) {
            String[] appResp = intent.getExtras().getStringArray(AppIntentService.RESULT);

            Log.i(TAG, "Response from command is " + appResp[0]);

            //check the type of command  for selecting correct actions
            if (appResp[0].equals("contact_find")) {
                StartMessage(getResources().getString(R.string.make_call_question));
                tel = appResp[1];
                setIsinteractive(true);
                setFirst(true);
            } else if (appResp[0].equals(context.getResources().getString(R.string.make_call_error_message))) {
                StartMessage(appResp[0]);
                setIsinteractive(false);
                setActivated(false);
            } else {
                StartMessage(appResp[0]);
                setIsinteractive(false);
                setActivated(false);

            }

        }

    }
}
