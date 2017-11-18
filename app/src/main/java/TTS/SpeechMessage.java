package TTS;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by bill on 11/10/17.
 */

public class SpeechMessage {

    private static TextToSpeech tts;
    private static String S_str;

    public static void Talk(Context context, String str) {
        S_str = str;
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.getDefault());
                    tts.setPitch(1.3f);
                    tts.setSpeechRate(1f);
                    //   tts.speak(SC_str, TextToSpeech.QUEUE_FLUSH, null,null);
                    tts.speak(S_str, TextToSpeech.QUEUE_FLUSH, null);


                }
            }
        });
    }

    public static void cancel() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }
}
