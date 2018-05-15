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
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

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

public class SpeechRegognition implements RecognitionListener {


    private final String TAG = this.getClass().getSimpleName();
    private SpeechRecognizer AssistantSpeechRegnizer;
    private Intent SpeechIntent;
    private Handler restartDroidSpeech = new Handler(), SpeechPartialResult = new Handler();
    private Boolean IsReadyForSpeach = false, speechResultFound = false;

    private long StartListeningTime, PauseAndSpeakTime;
    private boolean continuousSpeechRecognition;
    private AudioManager audioManager;
    private Context context;
    private boolean isActivated;

    public SpeechRegognition(Context context) {
        this.context = context;

        Init();

    }

    private void Init() {
        Log.i(TAG, "Initialize parameters");
        createSpeechIntent();
        AssistantSpeechRegnizer = SpeechRecognizer.createSpeechRecognizer(context);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    private void createSpeechIntent() {
        SpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        SpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //SpeechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500);
    }

    // functions for boolean continuous recognition
    public boolean isContinuousSpeechRecognition() {

        return continuousSpeechRecognition;
    }

    public void setContinuousSpeechRecognition(boolean continuousSpeechRecognition) {
        Log.i(TAG, "set continuous recognition " + continuousSpeechRecognition);
        this.continuousSpeechRecognition = continuousSpeechRecognition;
    }

    //function for mute and unmute audio
    public void MuteAudio(Boolean mute) {
        Log.i(TAG, "mute parameter is " + mute);

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

        //take the specific time of start listening
        StartListeningTime = System.currentTimeMillis();
        PauseAndSpeakTime = StartListeningTime;
        speechResultFound = false;

        if (SpeechIntent == null || AssistantSpeechRegnizer == null || audioManager == null) {
            Log.i(TAG, "initializition if null");
            Init();
        }

        AssistantSpeechRegnizer.setRecognitionListener(this);
        // Canceling any running  speech operations, before listening
        if (!isContinuousSpeechRecognition()) {
            CancelSpeechRecognizer();
        }
        // Start Listening
        AssistantSpeechRegnizer.startListening(SpeechIntent);
    }

    public void CancelSpeechRecognizer() {
        if (AssistantSpeechRegnizer != null) {
            Log.i(TAG, "cancel speech recognize");
            AssistantSpeechRegnizer.cancel();
        }
        //  MuteAudio(false);

    }

    public void CloseSpeechRegognizer() {

        if (AssistantSpeechRegnizer != null) {
            Log.i(TAG, "destroy speech recognize");
            AssistantSpeechRegnizer.destroy();
        }
        SpeechPartialResult.removeCallbacksAndMessages(null);

    }


    //function for restarting speech recognition after 500 milliseconds delay
    private void restartSpeechRegognizer() {


        restartDroidSpeech.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "Restart speech recognize");
                StartSpeechRegognize();

            }

        }, 300);

    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(TAG, "ready for speaking");

        // MuteAudio(false);
        IsReadyForSpeach = true;

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Constatns.app.Init();
        }



        // If duration is less than the "error timeout" as the system didn't try listening to the user speech so ignoring
        long duration = System.currentTimeMillis() - StartListeningTime;
        if (duration < 5000 && i == Constants.ErrorNoMatch && !IsReadyForSpeach) {
            Log.i(TAG, "no match and duration is : " + duration);
            return;
        }


        if (Constants.ErrorNoMatch == i || Constants.ErrorSpeechTimeOut == i || Constants.ErrorAudio == i) {
            if (continuousSpeechRecognition) {
                Log.i(TAG, "if no match found restarting.. ");
                restartSpeechRegognizer();
            }else{
                //close recognition if not continuous
                if (isActivated()) {
                    EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
                }

            }

        }
        EventBus.getDefault().postSticky(new Events.PartialResults(""));
        if (isActivated()) {
            EventBus.getDefault().post(new Events.SpeechMessage("Η αναγνώριση τερματίζει",false));

        }

        setActivated(false);



        //mute audio beep
        MuteAudio(true);
    }

    @Override
    public void onResults(Bundle results) {

        Log.i(TAG, "final results: " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));

        if (speechResultFound) {
            Log.i(TAG, "If results found returning");
            //  MuteAudio(true);
            return;
        }

        speechResultFound = true;

        Boolean valid = (
                results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION) &&
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null &&
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0 &&
                        !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).trim().isEmpty()
        );

        if (valid) {
            // Getting the speech final result

            if (isActivated() && !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).equals("")) {
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(WitResponse.GetResults(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0)));
            } else if (results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).equals("Ίριδα")) {

                MuteAudio(false);
                EventBus.getDefault().post(new Events.SpeechMessage("Παρακαλώ, πείτε μου πως μπορώ να βοηθήσω",true));
                setActivated(true);
            }
            if (continuousSpeechRecognition) {
                Log.i(TAG, "speech start again");
                // Start  speech recognition again
                StartSpeechRegognize();
            } else {
                // Closing the  speech operations
                 CloseSpeechRegognizer();




            }
            EventBus.getDefault().postSticky(new Events.PartialResults(""));

        } else {
            // No match found, restart  speech recognition
            Log.i(TAG, "If no results restarting");

            restartSpeechRegognizer();

        }
    }

    @Override
    public void onPartialResults(Bundle results) {
        if (speechResultFound) {
            Log.i(TAG, "If partial results found returning");
            //  MuteAudio(true);
            return;
        }
        Boolean valid = (
                results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION) &&
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null &&
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() > 0 &&
                        !results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).trim().isEmpty()
        );

        final String partialResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);

        if (valid) {

            Log.i(TAG, "pause time: " + PauseAndSpeakTime + " current mills: " + System.currentTimeMillis());

            if(isActivated()){
                EventBus.getDefault().postSticky(new Events.PartialResults(partialResult));
            }

            //if the current time (that receive partial result) subtraction with the start time of listening is 500 milliseconds
            // close recognition and restart it after 500 milliseconds
            if ((System.currentTimeMillis() - PauseAndSpeakTime) > 350) {
                speechResultFound = true;

                SpeechPartialResult.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "On partial result closing speech"+partialResult);

                        CloseSpeechRegognizer();

                        if (isActivated() && !partialResult.equals("")) {
                            RequestQueue queue = Volley.newRequestQueue(context);
                            queue.add(WitResponse.GetResults(partialResult));
                            EventBus.getDefault().post(new Events.ComputingRecognition(true));
                        } else if (partialResult.equals("Ίριδα")) {

                            MuteAudio(false);
                            EventBus.getDefault().post(new Events.SpeechMessage("Παρακαλώ, πείτε μου πως μπορώ να βοηθήσω",true));
                            setActivated(true);
                        }
                        if (continuousSpeechRecognition) {
                            Log.i(TAG, "on partial start speech again");
                            // Start  speech recognition again
                            StartSpeechRegognize();
                        } else {
                            Log.i(TAG, "on partial stop speech");
                            // Closing the  speech operations
                             CloseSpeechRegognizer();
                            //EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
                        }
                        EventBus.getDefault().postSticky(new Events.PartialResults(""));

                    }
                }, 350);

            } else {
                PauseAndSpeakTime = System.currentTimeMillis();
            }

        } else {
            PauseAndSpeakTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        //NA

    }
    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        Log.i(TAG,"boolean activated is "+activated);
        isActivated = activated;

    }

}