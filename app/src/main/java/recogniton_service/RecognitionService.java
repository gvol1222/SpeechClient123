package recogniton_service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.bill.Activities.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import applications.Constants;
import events.Events;
import recognize.SpeechRecognition;
import tts.SpeechHelper;
import tts.TtsProgressListener;

/**
 * Created by bill on 11/30/17.
 */

//this class contains functions of speech recognizer
public abstract class RecognitionService extends Service implements TtsProgressListener {

    private final String TAG = this.getClass().getSimpleName();
    protected SpeechRecognition recognition;
    private Handler startHandler;
    private Handler closeHandler;
    private SpeechHelper TalkEngine;

    private Handler mHandler;

    //broadcast for actions on clicking notification
    private final BroadcastReceiver NotAction = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            speak(getResources().getString(R.string.StartMessage));
        }
    };

    @Subscribe
    public void OnSpeechMessage(Events.SpeechMessage event) {

        String message = event.getSpeechMessage();
        speak(message);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        InitHandler();
        Init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        free();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onStartTalk() {
        //on start talking assistant close recognition and enable beep
        EventBus.getDefault().postSticky(new Events.ActivatedRecognition (false));
        Log.e("Start Talk", "Start");
        runCloseSpeech();

    }


    @Override
    public void onEndTalk() {
        //on end talking assistant start recognition
        Log.e("End Talk", "End");
        EventBus.getDefault().postSticky(new Events.ActivatedRecognition (true));
        startRecognition();

    }


    private void Init() {
        Log.i(TAG, "Initialization object and messages");

        TalkEngine = new SpeechHelper(getApplicationContext(), this);
        registerReceiver(NotAction, new IntentFilter(Constants.NOTIFICATION_ACTION));
        setRecognition();
    }

    //close and destroy speech recognition
    private void free() {
        Log.i(TAG, "Free resources");
        if (recognition != null) {
            recognition.CancelSpeechRecognizer();
            recognition = null;
        }
        if (TalkEngine != null)
            TalkEngine.cancel();

        unregisterReceiver(NotAction);

    }

    public void StartMessage(final String msg) {
        ToastMessage(msg);
        TalkEngine.speak(msg);
        EventBus.getDefault().postSticky(new Events.SpeechMessageShow (msg));
    }

    public void ToastMessage(final String msg) {
        mHandler.post(() -> Toast.makeText(RecognitionService.this, msg, Toast.LENGTH_SHORT).show());
    }

    //initiate handler for running on main thread
    private void InitHandler() {
        Log.i(TAG, "Initialization of handlers");
        startHandler = new Handler(Looper.getMainLooper());
        closeHandler = new Handler(Looper.getMainLooper());
    }

    public void setRecognition() {
        Log.i(TAG, "Recognition created");
        recognition = new SpeechRecognition(getApplicationContext());


    }

    //Helpers to Start-Stop Recognition

    public void StopSrecognition() {
        if (recognition != null)
            recognition.CancelSpeechRecognizer();


    }

    public void StartRecognition() {
        if (recognition != null) {
            startService(new Intent(this, Maestro.class));
            recognition.StartSpeechRecognize();
        }
    }

    public void runCloseSpeech() {
        closeHandler.post(() -> {
            StopSrecognition();
            Log.i(TAG, "recognition closed");
        });
    }

    public void startRecognition() {

        startHandler.post(() -> StartRecognition());
    }


    public void speak(String message) {

        StartMessage(message);
    }


}