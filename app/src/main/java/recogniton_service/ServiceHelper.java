package recogniton_service;

import android.content.Intent;
import android.widget.Toast;

import com.example.bill.Activities.R;

import recognize.AssistanListener;
import tts.SpeecHelper;
import tts.TtsProgressListener;

/**
 * Created by bill on 11/28/17.
 */

public abstract class ServiceHelper extends RecognitionService implements AssistanListener, TtsProgressListener {

    private SpeecHelper talkengine;

    private String startMessage = "";
    private String waitMessage = "";
    private boolean isActivated;


    @Override
    public void onCreate() {
        super.onCreate();
        Init();
    }

    private void Init() {
        talkengine = new SpeecHelper(getApplicationContext(), this);
        startMessage = getApplicationContext().getResources().getString(R.string.StartMessage);
        waitMessage = getApplicationContext().getResources().getString(R.string.WaitMessage);
        SetListener(this);
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

    @Override
    public void onStartTalk() {
        runCloseSpeech();
    }


    @Override
    public void onEndTalk() {
        runStartSpeech();
    }


    @Override
    public void setFirst(boolean first) {
        super.setFirst(first);
    }

    @Override
    public void OnSpeechLiveResult(String LiveResult) {
    }

    @Override
    public void OnSpeechResult(String Result) {

    }

    @Override
    public void OnSpeechError(int Error) {
        isActivated = false;
        CancelOnNotContinous();
    }

    @Override
    public void onEndOfSpeech() {
        //   StartMessage(waitMessage);
    }

    public void StartInteract() {
        isActivated = true;

        if (!isContinuousSpeechRecognition()) {
            setFirst(false);
        }
        StartMessage(startMessage);
    }

    public void StartMessage(String msg) {
        talkengine.speak(msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    //free resources
    private void free() {
        if (talkengine != null)
            talkengine.cancel();
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }
}
