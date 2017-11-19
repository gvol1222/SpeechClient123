package TTS;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by bill on 11/10/17.
 */

public class SpeechMessage extends TextToSpeech {
    public SpeechMessage(Context context, OnInitListener listener) {
        super(context, listener);
    }

    public void talk(String S_str) {
        this.setLanguage(Locale.getDefault());
        this.setPitch(1.3f);
        this.setSpeechRate(1f);
        this.speak(S_str, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void cancel() {

        if (this != null) {
            this.stop();
            this.shutdown();
        }

    }
}
