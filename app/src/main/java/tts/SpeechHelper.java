package tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Locale;

import events.Events;

/**
 * Created by bill on 11/23/17.
 */

public class SpeechHelper implements TextToSpeech.OnInitListener {
    private static final String TAG = "SpeechHelper";
    private final TextToSpeech tts;
    private final TtsProgressListener ttsListener;
    private  boolean isTalking;


    //initiate text to speech api and map
    public SpeechHelper(Context context, TtsProgressListener ttsListener) {
        this.ttsListener = ttsListener;
        tts = new TextToSpeech(context.getApplicationContext(), this);

        tts.setPitch(1.3f);
        tts.setSpeechRate(1f);
        Log.i(TAG, "intiatiated successfully");
    }

    //function for speaking text to speech
    public void speak(String msg) {
        Log.i(TAG, "call speak");

        tts.speak(msg, TextToSpeech.QUEUE_FLUSH,null,"UniqueID");

    }

    public void setIsTalking(boolean isTalking){
        this.isTalking = isTalking;
    }
    public boolean getIsTalking(){
        return this.isTalking ;
    }
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {
                    Log.i(TAG, "start speaking");
                    ttsListener.onStartTalk();

                }

                @Override
                public void onDone(String s) {
                    Log.i(TAG, "end speaking");
                    ttsListener.onEndTalk();

                }

                @Override
                public void onError(String error) {
                    Log.i(TAG, "something goes wrong error is : " + error);

                }
            });

        }

    }

    //stops text to speech api
    public void cancel() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }
}
