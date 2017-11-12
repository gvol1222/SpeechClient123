package Recognize;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by gvol on 12/11/2017.
 */

public class Interaction {

    public static String OnNonValidCommand(Context context) {
        TextToSpeech tt = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });

        tt.setLanguage(Locale.getDefault());
        tt.speak("Παρακαλώ επαναλάβετε την εντολή", TextToSpeech.QUEUE_FLUSH, null);
        return "";
    }
}
