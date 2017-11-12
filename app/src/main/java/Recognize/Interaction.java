package Recognize;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by gvol on 12/11/2017.
 */

public class Interaction {
    private static TextToSpeech tts;

    public static String OnNonValidCommand(Context context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.getDefault());
                    tts.setPitch(1.3f);
                    tts.setSpeechRate(1f);
                    tts.speak("Παρακαλώ, επαναλάβετε την εντολή.", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    return null;
    }
}
