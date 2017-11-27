package recogniton_service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import applications.AppIntentService;
import recognize.AssistanListener;
import recognize.SpeechRegognition;
import tts.SpeecHelper;
import tts.TtsProgressListener;
import wit_connection.WitResponse;
import wit_connection.WitResponseMessage;

/**
 * Created by bill on 11/14/17.
 */

public class SpeechService extends Service implements AssistanListener, WitResponseMessage, TtsProgressListener {
    public static final String BroadcastAction = "com.example.bill.Activities.MainActivity.UpdateGui";
    private static final String TAG = "BtroadCast";
    private final IBinder assistantBinder = new AssistantBinder();
    private SpeecHelper talkengine;
    private SpeechRegognition recognition;
    private boolean isActivated;
    private ResponseReceiver receiver;
    private Handler handler, handler2;
    private Handler broadCastHandler;
    private boolean isready;
    private Intent broadcastIntent;
    private String result = "";
    private Runnable UpdateUi = new Runnable() {
        @Override
        public void run() {
            DisplayUi();
            broadCastHandler.post(this);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        handler2 = new Handler(Looper.getMainLooper());

        broadCastHandler = new Handler();
        broadcastIntent = new Intent(BroadcastAction);


        talkengine = new SpeecHelper(getApplicationContext(), this);
        setRecognition();
        recognition.setContinuousSpeechRecognition(true);

        IntentFilter broadcastFilter = new IntentFilter(ResponseReceiver.LOCAL_ACTION);
        receiver = new ResponseReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver, broadcastFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    private void DisplayUi() {
        broadcastIntent.putExtra("result", result);
        sendBroadcast(broadcastIntent);
    }

    public void starIneract() {

        if (!recognition.isContinuousSpeechRecognition()) {
            talkengine.setFirst(false);
            isActivated = true;
        } else {
            isActivated = true;
        }

        Log.i("activated", String.valueOf(talkengine.isFirst()));
        talkengine.speak("Πείτε μου πως μπορώ να βοηθήσω");
        Toast.makeText(this, "Πείτε μου πως μπορώ να βοηθήσω", Toast.LENGTH_LONG).show();


    }

    //set recognition
    private void setRecognition() {
        recognition = new SpeechRegognition(getApplicationContext());
        recognition.setListener(this);
        recognition.setContinuousSpeechRecognition(true);
    }


    @Override
    public void onDestroy() {

        if (recognition != null) {
            recognition.CloseSpeechRegognizer();
            recognition = null;
        }
        talkengine.cancel();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //  broadCastHandler.removeCallbacksAndMessages(UpdateUi);
        //broadCastHandler.post(UpdateUi);
        return assistantBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (recognition != null) {
            recognition.CloseSpeechRegognizer();
            recognition = null;
        }
        talkengine.cancel();
        broadCastHandler.removeCallbacks(UpdateUi);
        return false;
    }

    @Override
    public void OnSpeechLiveResult(String LiveResult) {
        if (isActivated) {
            broadcastIntent.putExtra("result", LiveResult);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void OnSpeechResult(String Result) {
        if (isActivated) {
            wit_connection.WitResponse witResponse = new WitResponse(this);
            witResponse.execute(Result);

        } else if (Result.equals("Γιάννη")) {
            Toast.makeText(this, "Πείτε μου πως μπορώ να βοηθήσω", Toast.LENGTH_LONG).show();
            talkengine.speak("Πείτε μου πως μπορώ να βοηθήσω");
            isActivated = true;

        }
    }

    @Override
    public void OnSpeechError(int Error) {
        isActivated = false;
        broadcastIntent.putExtra("result", "");
        sendBroadcast(broadcastIntent);
        if (!recognition.isContinuousSpeechRecognition())
            recognition.CancelSpeechRecognizer();

    }

    private void runOnUiThread() {

        handler.post(new Runnable() {
            @Override
            public void run() {

                if (!recognition.isContinuousSpeechRecognition()) {
                    if (talkengine.isFirst()) {
                        talkengine.setFirst(false);
                        recognition.StartSpeechRegognize();

                    }
                } else {
                    recognition.StartSpeechRegognize();

                }
            }
        });
    }

    private void runOnUiThreadStart() {
        handler2.post(new Runnable() {
            @Override
            public void run() {
                recognition.CancelSpeechRecognizer();

            }
        });
    }

    @Override
    public void onEndTalk() {
        runOnUiThread();

    }

    @Override
    public void onStartTalk() {
        runOnUiThreadStart();


    }

    @Override
    public void onInitTts(int status, int result) {
        if (status == TextToSpeech.SUCCESS) {
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isready = true;
            }
        }
    }

    @Override
    public void ErrorOnCommand(String msg) {

        if (isActivated) {
            talkengine.speak(msg);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void ErrorCommand(String msg) {
        if (isActivated) {
            talkengine.speak(msg);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void Message(String search, String application, String conf) {
        Intent newint = new Intent(this, AppIntentService.class);
        Log.d("ATTENTION", search);
        Log.d("ATTENTION", application);
        broadcastIntent.putExtra("result", "");
        sendBroadcast(broadcastIntent);
        newint.putExtra(AppIntentService.APP_KIND, application);
        newint.putExtra(AppIntentService.QUERY, search);
        startService(newint);
    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String LOCAL_ACTION =
                "com.example.bill.speechclient.applications.appintentservice.COMMAND_DONE";

        @Override
        public void onReceive(Context context, Intent intent) {
            String appresp = intent.getStringExtra(AppIntentService.RESULT);
            talkengine.speak(appresp);
            Toast.makeText(context, appresp, Toast.LENGTH_LONG).show();
            if (!recognition.isContinuousSpeechRecognition()) {
                recognition.CancelSpeechRecognizer();

            }

            isActivated = false;

        }
    }


    public class AssistantBinder extends Binder {
        public SpeechService getService() {
            return SpeechService.this;
        }
    }
}
