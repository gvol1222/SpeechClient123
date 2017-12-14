package recogniton_service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import recognize.AssistanListener;
import recognize.SpeechRegognition;

/**
 * Created by bill on 11/30/17.
 */

//this class contains functions of speech recognizer
public abstract class RecognitionService extends Service {

    private final String TAG = this.getClass().getSimpleName();
    private SpeechRegognition recognition;
    private Handler startHandler;
    private Handler closeHandler;
    private boolean isFirst;


    @Override
    public void onCreate() {
        super.onCreate();
        InitHandler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        free();
    }

    //close and destroy speech recognition
    private void free() {
        Log.i(TAG, "Free resources");
        if (recognition != null) {
            recognition.CloseSpeechRegognizer();
            recognition = null;
        }
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

    //set speech recognition for continuous recognition o not continuous
    public void setContinuous(boolean continuous) {
        Log.i(TAG, "continuous parameter is " + continuous);
        recognition.setContinuousSpeechRecognition(continuous);

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


}
