package recognize;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import applications.Constatns;
import events.Events;
import wit_connection.WitResponse;

/**
 * Created by bill on 11/1/17.
 */

public class SpeechRecognition implements RecognitionListener {


    private final String TAG = this.getClass().getSimpleName();
    private SpeechRecognizer AssistantSpeechRecognizer;
    private Intent SpeechIntent;
    private Handler SpeechPartialResult ;
    private Boolean  speechResultFound = false;

    private long StartListeningTime, PauseAndSpeakTime;
    private boolean continuousSpeechRecognition;
    private AudioManager audioManager;
    private Context context;

    public boolean isTalking;

    public SpeechRecognition(Context context) {
        this.context = context;

        Init();

    }

    private void Init() {
        Log.i(TAG, "Initialize parameters");
        SpeechPartialResult = new Handler();
        createSpeechIntent();
        AssistantSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    private void createSpeechIntent() {
        SpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    }


    public void StartSpeechRegognize() {

        Log.i(TAG, "start recognize");

        //take the specific time of start listening
        StartListeningTime = System.currentTimeMillis();
        PauseAndSpeakTime = StartListeningTime;
        speechResultFound = false;

        if (SpeechIntent == null || AssistantSpeechRecognizer == null || audioManager == null) {
            Log.i(TAG, "initializition if null");
            Init();
        }

        AssistantSpeechRecognizer.setRecognitionListener(this);
        // Canceling any running  speech operations, before listening

        // Start Listening
        AssistantSpeechRecognizer.startListening(SpeechIntent);
    }

    public void CancelSpeechRecognizer() {
        if (AssistantSpeechRecognizer != null) {
            Log.i(TAG, "cancel speech recognize");
            AssistantSpeechRecognizer.cancel();
        }


    }

    public void CloseSpeechRecognizer() {

        if (AssistantSpeechRecognizer != null) {
            Log.i(TAG, "destroy speech recognize");
            AssistantSpeechRecognizer.destroy();
        }
        SpeechPartialResult.removeCallbacksAndMessages(null);

    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(TAG, "ready for speaking");


    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(TAG, "beginning of speaking");

    }

    @Override
    public void onRmsChanged(float v) {
        //NA

    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        //NA
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(TAG, "end of speeking");

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onError(int i) {
        Log.i(TAG, "error code: " + i);



        Constatns.app.Init();


        // If duration is less than the "error timeout" as the system didn't try listening to the user speech so ignoring
        long duration = System.currentTimeMillis() - StartListeningTime;
        if (i == Constants.ErrorNoMatch ) {
            Log.i(TAG, "no match and duration is : " + duration);
            return;
        }
        EventBus.getDefault().postSticky(new Events.PartialResults(""));

    }

    @Override
    public void onResults(Bundle results) {

        Log.i(TAG, "final results: " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));

        if (speechResultFound ) {
            Log.i(TAG, "If results found returning");
            return;
        }

        speechResultFound = true;

        boolean valid = (
                results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION) &&
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null &&
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0 &&
                        !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).trim().isEmpty()
        );

        if (valid) {
            // Getting the speech final result

            if (!results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).equals("")) {
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(WitResponse.GetResults(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0)));

                Log.i(TAG, "Results");
            }

                // Closing the  speech operations
            CloseSpeechRecognizer();

            EventBus.getDefault().postSticky(new Events.PartialResults(""));

        }


    }

    @Override
    public void onPartialResults(Bundle results) {
        if (speechResultFound ) {
            Log.i(TAG, "If partial results found returning");
            //  MuteAudio(true);
            return;
        }
        boolean valid = (
                results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION) &&
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null &&
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0 &&
                        !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).trim().isEmpty()
        );

        final String partialResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);

        if (valid) {

            Log.i(TAG, "pause time: " + PauseAndSpeakTime + " current mills: " + System.currentTimeMillis());
            EventBus.getDefault().postSticky(new Events.PartialResults(partialResult));

        }
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        //NA

    }


}