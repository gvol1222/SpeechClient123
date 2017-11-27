package tts;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

import activities.MainActivity;
import recognize.SpeechRegognition;

/**
 * Created by bill on 11/23/17.
 */

public class SpeecHelper implements TextToSpeech.OnInitListener {
    private static final HashMap<String, String> map = new HashMap<String, String>();
    private TextToSpeech tts;
    private MainActivity activity;
    private boolean first = true;
    private SpeechRegognition recognition;
    private TtsProgressListener ttsListener;
    private boolean isready = false;


    public SpeecHelper(MainActivity activity, TtsProgressListener ttsListener) {
        this.activity = activity;
        this.ttsListener = ttsListener;
        tts = new TextToSpeech(activity.getApplicationContext(), this);
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
    }

    public SpeecHelper(Context context, TtsProgressListener ttsListener) {

        this.ttsListener = ttsListener;
        tts = new TextToSpeech(context.getApplicationContext(), this);
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        tts.setPitch(1.3f);
        tts.setSpeechRate(1f);
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {

        this.first = first;
    }

    private void runThread() {

        new Thread() {
            public void run() {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (!recognition.isContinuousSpeechRecognition()) {
                            if (first) {
                                first = false;
                                recognition.StartSpeechRegognize();

                            }
                        } else {
                            recognition.StartSpeechRegognize();

                        }
                    }
                });

            }
        }.start();
    }

    private void installTTS() {
        Intent installIntent = new Intent();
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        activity.startActivity(installIntent);
    }

    public void speak(String msg) {
        Log.d("tag", "startRecord: ");

       /* tts.setLanguage(Locale.getDefault());
        tts.setPitch(1.3f);
        tts.setSpeechRate(1f);
        */
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, map);
    }

    public boolean isIsready() {
        return isready;
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.getDefault());
            ttsListener.onInitTts(status, result);
            isready = true;
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {
                    ttsListener.onStartTalk();
               /*     new Thread() {
                        public void run() {
                            activity.runOnUiThread(new Runnable() {

                                @Override
                                public synchronized void run() {

                                    recognition.CancelSpeechRecognizer();

                                }
                            });

                        }
                    }.start();*/
                }

                @Override
                public void onDone(String s) {
                    ttsListener.onEndTalk();
                    //runThread();
                }

                @Override
                public void onError(String s) {

                }
            });

        } else {
            installTTS();
        }

    }

    public void cancel() {

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
