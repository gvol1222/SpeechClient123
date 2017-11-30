package tts;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by bill on 11/23/17.
 */

public class SpeecHelper implements TextToSpeech.OnInitListener {
    private static final HashMap<String, String> map = new HashMap<String, String>();
    private TextToSpeech tts;
    private TtsProgressListener ttsListener;

    public SpeecHelper(Context context, TtsProgressListener ttsListener) {

        this.ttsListener = ttsListener;

        tts = new TextToSpeech(context.getApplicationContext(), this);
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        tts.setPitch(1.3f);
        tts.setSpeechRate(1f);
    }

    public void speak(String msg) {
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, map);
    }


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {
                    ttsListener.onStartTalk();

                }

                @Override
                public void onDone(String s) {
                    ttsListener.onEndTalk();

                }

                @Override
                public void onError(String s) {

                }
            });

        }

    }

    public void cancel() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
