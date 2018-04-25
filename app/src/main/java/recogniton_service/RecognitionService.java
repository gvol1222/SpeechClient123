package recogniton_service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.bill.Activities.R;

import recognize.AssistanListener;
import recognize.SpeechRegognition;
import tts.SpeecHelper;
import tts.TtsProgressListener;

import static recogniton_service.SpeechService.BroadcastAction;

/**
 * Created by bill on 11/30/17.
 */

//this class contains functions of speech recognizer
public abstract class RecognitionService extends Service implements AssistanListener, TtsProgressListener {

    private final String TAG = this.getClass().getSimpleName();
    private SpeechRegognition recognition;
    private Handler startHandler;
    private Handler closeHandler;
    private boolean isFirst;
    private SpeecHelper talkengine;
    private String startMessage = "";
    private String waitMessage = "";
    private boolean isActivated;
    private boolean isFinishedTts;
    Intent broadcastIntent;

    @Override
    public void onCreate() {
        super.onCreate();
         broadcastIntent = new Intent(BroadcastAction);
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
        free();
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

        Mute(false);
        runCloseSpeech();
    }


    @Override
    public void onEndTalk() {
        //on end talking assistant start recognition
        broadcastIntent.putExtra("ripple", "ripple");
        sendBroadcast(broadcastIntent);
        isFinishedTts =true;
        runStartSpeech();

    }
    //set speech recognition for continuous recognition o not continuous
    public void setContinuous(boolean continuous) {
        Log.i(TAG, "continuous parameter is " + continuous);
        recognition.setContinuousSpeechRecognition(continuous);

    }
    private void Init() {
        Log.i(TAG, "Initialization object and messages");
        isFinishedTts =true;
        talkengine = new SpeecHelper(getApplicationContext(), this);
        startMessage = getApplicationContext().getResources().getString(R.string.StartMessage);
        waitMessage = getApplicationContext().getResources().getString(R.string.WaitMessage);
        setRecognition(this);
    }
    //close and destroy speech recognition
    private void free() {
        Log.i(TAG, "Free resources");
        if (recognition != null) {
            recognition.CloseSpeechRegognizer();
            recognition = null;
        }
        if (talkengine != null)
            talkengine.cancel();

    }

    public void StartMessage(String msg) {

        talkengine.speak(msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    //initiate handler for running on main thread
    private void InitHandler() {
        Log.i(TAG, "Initialization of handlers");
        startHandler = new Handler(Looper.getMainLooper());
        closeHandler = new Handler(Looper.getMainLooper());
    }

    public void setRecognition(AssistanListener listener) {
        Log.i(TAG, "Recognition created");
        recognition = new SpeechRegognition(getApplicationContext(), listener);

    }

    public void CancelOnNotContinuous() {
        if (!recognition.isContinuousSpeechRecognition()) {
            StopSrecognition();
        }
    }

    public void StopSrecognition() {
        if (recognition != null)
            recognition.CancelSpeechRecognizer();


    }

    public void StartRecognition() {
        if (recognition != null)
            recognition.StartSpeechRegognize();
    }

    //mute and unmute beep sound
    public void Mute(Boolean mute) {
        recognition.MuteAudio(mute);

    }

    //this functions that start and close the speech recognition must run on the main thread
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
                if (!recognition.isContinuousSpeechRecognition()) {
                    if (isFirst) {
                        isFirst = false;
                        StartRecognition();
                    }
                } else {
                    StartRecognition();
                }
            }
        });
    }

    protected boolean isContinuousSpeechRecognition() {
        return recognition.isContinuousSpeechRecognition();
    }

    //this function is usefull for not continuous recognition
    public void setFirst(boolean first) {
        Log.i(TAG, "first parameter is " + first);
        isFirst = first;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        Log.i(TAG,"boolean activated is "+activated);
        isActivated = activated;

    }

    public boolean isFinishedTts() {
        return isFinishedTts;
    }

    public void setFinishedTts(boolean finishedTts) {
        isFinishedTts = finishedTts;
    }

    //function for starting tts speaking
    public void StartInteract() {
        Log.i(TAG, "Assistant starting speaking");
        isActivated = true;

        StartMessage(startMessage);
        if (!isContinuousSpeechRecognition())
            setFirst(true);
    }
}
