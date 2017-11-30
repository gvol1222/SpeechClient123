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

public class RecognitionService extends Service {

    private SpeechRegognition recognition;
    private Handler startHandler;
    private Handler closeHandler;
    private boolean isFirst;


    @Override
    public void onCreate() {
        super.onCreate();
        InitHandler();
        setRecognition();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        free();
    }

    private void free() {
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

    private void InitHandler() {
        startHandler = new Handler(Looper.getMainLooper());
        closeHandler = new Handler(Looper.getMainLooper());
    }

    private void setRecognition() {
        recognition = new SpeechRegognition(getApplicationContext());
        recognition.setContinuousSpeechRecognition(true);
    }

    protected void SetListener(AssistanListener listener) {
        recognition.setListener(listener);

    }

    public void CancelOnNotContinous() {
        if (!recognition.isContinuousSpeechRecognition())
            StopSrecognition();
    }

    public void StopSrecognition() {
        if (recognition != null)
            recognition.CancelSpeechRecognizer();
    }

    public void StartRecognition() {
        if (recognition != null)
            recognition.StartSpeechRegognize();
    }

    public void runCloseSpeech() {
        closeHandler.post(new Runnable() {
            @Override
            public void run() {
                StopSrecognition();
                Log.i("close: ", "closed");
            }
        });
    }

    public void runStartSpeech() {

        startHandler.post(new Runnable() {
            @Override
            public void run() {

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

    public void setFirst(boolean first) {
        isFirst = first;
    }


}
