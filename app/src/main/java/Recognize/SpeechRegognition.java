package Recognize;

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

import WitConnection.CallWitResponse;

/**
 * Created by bill on 11/1/17.
 */

public class SpeechRegognition implements RecognitionListener, CallWitResponse {


    private Context context;
    private SpeechRecognizer AssistantSpeechRegnizer;
    private Intent SpeechIntent;
    private Handler restartDroidSpeech = new Handler(), SpeechPartialResult = new Handler();
    private Boolean IsReadyForSpeach = false, speechResultFound = false;
    private AssistanListener listener;
    private long StartListeningTime, PauseAndSpeakTime;
    private boolean continuousSpeechRecognition = true;
    private AudioManager audioManager;

    public SpeechRegognition(Context context) {
        this.context = context;
        Init();
    }

    private void Init() {
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
        this.listener = listener;
    }

    public boolean isContinuousSpeechRecognition() {
        return continuousSpeechRecognition;
    }

    public void setContinuousSpeechRecognition(boolean continuousSpeechRecognition) {
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


        StartListeningTime = System.currentTimeMillis();
        PauseAndSpeakTime = StartListeningTime;
        speechResultFound = false;
        if (SpeechIntent == null || AssistantSpeechRegnizer == null || audioManager == null) {
            Init();
        }

        AssistantSpeechRegnizer.setRecognitionListener(this);
        CancelSpeechRecognizer();
        AssistantSpeechRegnizer.startListening(SpeechIntent);
    }

    public void CancelSpeechRecognizer() {
        if (AssistantSpeechRegnizer != null) {
            AssistantSpeechRegnizer.cancel();
        }

    }

    public void CloseSpeechRegognizer() {
        if (AssistantSpeechRegnizer != null)
            AssistantSpeechRegnizer.destroy();

        MuteAudio(false);
    }


    private void restartSpeechRegognizer() {

        restartDroidSpeech.postDelayed(new Runnable() {

            @Override
            public void run() {
                StartSpeechRegognize();

            }

        }, Constants.DelayRestartTime);

    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        MuteAudio(false);
        IsReadyForSpeach = true;

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {
        Log.i("error", String.valueOf(i));


        long duration = System.currentTimeMillis() - StartListeningTime;
        if (duration < 5000 && i == Constants.ErrorNoMatch && !IsReadyForSpeach) return;

        if (IsReadyForSpeach && duration < 30000) {
            MuteAudio(true);
        } else {
            MuteAudio(false);
        }

        if (Constants.ErrorNoMatch == i || Constants.ErrorSpeechTimeOut == i || Constants.ErrorAudio == i) {
            restartSpeechRegognizer();
        } else if (listener == null) {

        }

    }

    @Override
    public void onResults(Bundle results) {

        if (speechResultFound) return;

        speechResultFound = true;
        MuteAudio(false);
        Boolean valid = (results != null && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION) &&
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null &&
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0 &&
                !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).trim().isEmpty());

        if (valid) {

            if (listener == null) {
                Log.i("listener: ", "Droid speech null listenr result = " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
            } else {
                listener.OnSpeechResult(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
                Log.i("finalresult: ", "Droid speech onresults result = " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));

                StartSpeechRegognize();
            }


        } else {
            restartSpeechRegognizer();

        }
    }

    @Override
    public void onPartialResults(Bundle results) {
        if (speechResultFound) return;

        Boolean valid = (results != null && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION) &&
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null &&
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0 &&
                !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).trim().isEmpty());

        final String partialResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
        if (valid) {

            if (listener == null) {
                Log.i("listener: ", "Droid speech final result = " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
            } else {
                listener.OnSpeechLiveResult(partialResult);
            }

            if ((System.currentTimeMillis() - PauseAndSpeakTime) > 500) {
                speechResultFound = true;
                SpeechPartialResult.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        CloseSpeechRegognizer();
                        if (listener == null) {
                            Log.i("listener: ", "Droid speech final result = " + partialResult);
                        } else {
                            Log.i("listener: ", "Droid 34 speech  partital result = " + partialResult);
                            listener.OnSpeechResult(partialResult);
                            StartSpeechRegognize();
                        }
                    }
                }, 500);

            } else {
                PauseAndSpeakTime = System.currentTimeMillis();
            }

        } else {
            PauseAndSpeakTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    @Override
    public void ResponseMsg(String msg) {

    }
}
