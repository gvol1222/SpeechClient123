package recognize;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.Locale;

/**
 * Created by bill on 11/1/17.
 */

public class SpeechRegognition implements RecognitionListener {


    private final String TAG = this.getClass().getSimpleName();
    private SpeechRecognizer AssistantSpeechRegnizer;
    private Intent SpeechIntent;
    private Handler restartDroidSpeech = new Handler(), SpeechPartialResult = new Handler();
    private Boolean IsReadyForSpeach = false, speechResultFound = false;
    private AssistanListener listener;
    private long StartListeningTime, PauseAndSpeakTime;
    private boolean continuousSpeechRecognition;
    private AudioManager audioManager;
    private Context context;

    public SpeechRegognition(Context context) {
        this.context = context;
        Init();
    }

    private void Init() {
        Log.i(TAG, "Initialize parameters");

        SpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        AssistantSpeechRegnizer = SpeechRecognizer.createSpeechRecognizer(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }


    public void setListener(AssistanListener listener) {
        Log.i(TAG, "set listenet ");
        this.listener = listener;
    }

    public boolean isContinuousSpeechRecognition() {
        return continuousSpeechRecognition;
    }

    public void setContinuousSpeechRecognition(boolean continuousSpeechRecognition) {
        Log.i(TAG, "set lcontinous " + continuousSpeechRecognition);
        this.continuousSpeechRecognition = continuousSpeechRecognition;
    }

    private void MuteAudio(Boolean mute) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        mute ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE, 0
                );

            } else {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute);
            }
        } catch (Exception error) {
            if (audioManager == null) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }


        }

    }

    public void StartSpeechRegognize() {


        Log.i(TAG, "start recognize");

        StartListeningTime = System.currentTimeMillis();
        PauseAndSpeakTime = StartListeningTime;
        speechResultFound = false;
        if (SpeechIntent == null || AssistantSpeechRegnizer == null || audioManager == null) {
            Log.i(TAG, "initializition if null");
            Init();
        }

        AssistantSpeechRegnizer.setRecognitionListener(this);
        CancelSpeechRecognizer();
        AssistantSpeechRegnizer.startListening(SpeechIntent);
    }

    public void CancelSpeechRecognizer() {
        if (AssistantSpeechRegnizer != null) {
            Log.i(TAG, "cancel speech recognize");

            AssistantSpeechRegnizer.cancel();
        }
        MuteAudio(false);

    }

    public void CloseSpeechRegognizer() {

        if (AssistantSpeechRegnizer != null) {
            Log.i(TAG, "destroy speech recognize");
            AssistantSpeechRegnizer.destroy();

        }
        SpeechPartialResult.removeCallbacksAndMessages(null);
        MuteAudio(false);
    }


    private void restartSpeechRegognizer() {

        restartDroidSpeech.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "Restart speech recognize");

                StartSpeechRegognize();

            }

        }, Constants.DelayRestartTime);

    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(TAG, "ready for speaking");

        MuteAudio(false);
        IsReadyForSpeach = true;

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "beginning of speaking");

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "end of speeking");
        listener.onEndOfSpeech();
    }

    @Override
    public void onError(int i) {
        Log.i(TAG, "error code: " + i);

        listener.OnSpeechError(i);
        MuteAudio(true);

        long duration = System.currentTimeMillis() - StartListeningTime;
        if (duration < 5000 && i == Constants.ErrorNoMatch && !IsReadyForSpeach) {
            Log.i(TAG, "no match and duration: " + duration);

            return;
        }

        if (IsReadyForSpeach && duration < 30000) {
            MuteAudio(true);
        }

        if (Constants.ErrorNoMatch == i || Constants.ErrorSpeechTimeOut == i || Constants.ErrorAudio == i) {
            if (continuousSpeechRecognition) {
                Log.i(TAG, "if no match restarting.. ");

                restartSpeechRegognizer();
            }

        } else if (listener == null) {
            Log.i(TAG, "something goes wrong! ");

        }

    }

    @Override
    public void onResults(Bundle results) {

        Log.i(TAG, "final results: " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));

        if (speechResultFound) {
            Log.i(TAG, "If results found returning");

            return;
        }

        speechResultFound = true;
        MuteAudio(false);
        Boolean valid = (results != null && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION) &&
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null &&
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0 &&
                !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).trim().isEmpty());

        if (valid) {

            if (listener == null) {
                Log.i(TAG, "Droid speech null result = " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
            } else {
                Log.i(TAG, "Droid speech result = " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));

                listener.OnSpeechResult(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
            }
            if (continuousSpeechRecognition) {
                Log.i(TAG, "speech start again");

                StartSpeechRegognize();
            } else {
                CloseSpeechRegognizer();
            }


        } else {
            Log.i(TAG, "If no results restarting");

            restartSpeechRegognizer();

        }
    }

    @Override
    public void onPartialResults(Bundle results) {
        if (speechResultFound) {
            Log.i(TAG, "If partial results found returning");
            return;
        }
        Boolean valid = (results != null && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION) &&
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null &&
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0 &&
                !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).trim().isEmpty());

        final String partialResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
        if (valid) {

            if (listener == null) {
                Log.i(TAG, "Droid speech error partial result = " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
            } else {
                Log.i(TAG, "If partial result: " + partialResult);

                listener.OnSpeechLiveResult(partialResult);
            }
            Log.i(TAG, "pause time: " + PauseAndSpeakTime + " current mills: " + System.currentTimeMillis());


            if ((System.currentTimeMillis() - PauseAndSpeakTime) > 500) {
                speechResultFound = true;

                SpeechPartialResult.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "On partial result closing speech");

                        CloseSpeechRegognizer();
                        if (listener == null) {
                            Log.i(TAG, " speech nullable error result = " + partialResult);
                        } else {
                            Log.i(TAG, "On partial  final result " + partialResult);
                            listener.OnSpeechResult(partialResult);
                        }
                        if (continuousSpeechRecognition) {
                            Log.i(TAG, "on partial start speech again");

                            StartSpeechRegognize();
                        } else {
                            Log.i(TAG, "on partial stop speech");

                            CloseSpeechRegognizer();
                        }

                    }
                }, 500);

            } else {
                PauseAndSpeakTime = System.currentTimeMillis();
            }/* */

        } else {
            PauseAndSpeakTime = System.currentTimeMillis();
        }/**/
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

}
