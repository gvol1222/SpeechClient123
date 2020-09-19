package recogniton_service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.bill.Activities.R;

import org.greenrobot.eventbus.Subscribe;

import applications.Constatns;
import events.Events;
import recognize.SpeechRegognition;
import tts.SpeecHelper;
import tts.TtsProgressListener;

/**
 * Created by bill on 11/30/17.
 */

//this class contains functions of speech recognizer
public abstract class RecognitionService extends Service implements  TtsProgressListener {

    private final String TAG = this.getClass().getSimpleName();
    protected SpeechRegognition recognition;
    private Handler startHandler;
    private Handler closeHandler;
    private boolean isFirst;
    private SpeecHelper talkengine;

    private boolean isFinishedTts;
    private Handler mHandler;

    //broadcast for actions on clicking notification
    private final BroadcastReceiver NotAction = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            speak(getResources().getString(R.string.StartMessage),true);
        }
    };
    @Subscribe
    public void OnSpeechMessage(Events.SpeechMessage event){
        boolean reocgnizeAfter = event.getRecognige_after();
        String message = event.getSpeechMessage();
        speak(message,reocgnizeAfter);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        InitHandler();
        Init();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        isFinishedTts =false;

        runCloseSpeech();

    }


    @Override
    public void onEndTalk() {
        //on end talking assistant start recognition
        isFinishedTts =true;

        runStartSpeech();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Init() {
        Log.i(TAG, "Initialization object and messages");
        isFinishedTts =true;
        talkengine = new SpeecHelper(getApplicationContext(), this);
        registerReceiver(NotAction, new IntentFilter(Constatns.NOT_ACTION));
        setRecognition();
    }
    //close and destroy speech recognition
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void free() {
        Log.i(TAG, "Free resources");
        if (recognition != null) {
            recognition.CloseSpeechRegognizer();
            recognition = null;
        }
        if (talkengine != null)
            talkengine.cancel();

        unregisterReceiver(NotAction);

    }

    public void StartMessage(final String msg) {


        ToasMessage(msg);
        talkengine.speak(msg);



    }
    public void ToasMessage(final String msg){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RecognitionService.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //initiate handler for running on main thread
    private void InitHandler() {
        Log.i(TAG, "Initialization of handlers");
        startHandler = new Handler(Looper.getMainLooper());
        closeHandler = new Handler(Looper.getMainLooper());
    }

    public void setRecognition() {
        Log.i(TAG, "Recognition created");
        recognition = new SpeechRegognition(getApplicationContext());


    }



    public void StopSrecognition() {
        if (recognition != null)
            recognition.CancelSpeechRecognizer();


    }

    public void StartRecognition() {
        if (recognition != null ) {
            startService(new Intent(this, Maestro.class));
            recognition.StartSpeechRegognize();
        }
    }

    public void runCloseSpeech() {
        closeHandler.post(new Runnable() {
            @Override
            public void run() {
                StopSrecognition();
                Log.i(TAG, "recognition closed");
            }
        });
    }
    public void runStartSpeech() {

        startHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "recognition started first: " + isFirst);
                StartRecognition();

            }
        });
    }




    public boolean isFinishedTts() {
        return isFinishedTts;
    }



    public void speak (String message,boolean recognize_after){

        StartMessage(message);
    }


}