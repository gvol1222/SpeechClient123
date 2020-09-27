package recognize;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import events.Events;
import wit_connection.WitResponse;
import applications.Constants;

public class SpeechRecognition implements RecognitionListener {
    private static final String TAG = "SpeechRecognizer";
    private final SpeechRecognizer speechRecognizer;
    private final Intent speechIntent;
    private final Context context;
    private Boolean  speechResultFound;
    public SpeechRecognition(Context context) {
        this.context = context;
        speechResultFound = false;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
       // speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,5000);


    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.e(TAG,"Beggining of Speech");

    }

    @Override
    public void onRmsChanged(float rmsdB) {
       // Log.e(TAG,"rms"+rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.e(TAG,"Buffer");
    }

    @Override
    public void onEndOfSpeech() {
        EventBus.getDefault().postSticky(new Events.ActivatedRecognition (false));
    }

    @Override
    public void onError(int error) {
        Log.i(TAG, "error code: " + error);

        Constants.app.Init();
        EventBus.getDefault().postSticky(new Events.ActivatedRecognition (false));
    }

    @Override
    public void onResults(Bundle results) {
        final String finalResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
        Log.i(TAG, "final results: " + finalResults);

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

            if (!finalResults.equals("")) {
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(WitResponse.GetResults(finalResults));

                Log.i(TAG, "Results");
            }

            EventBus.getDefault().postSticky(new Events.Results(finalResults));
            // Closing the  speech operations
            CancelSpeechRecognizer();
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        final String partialResult = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
        EventBus.getDefault().postSticky(new Events.PartialResults(partialResult));

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public void StartSpeechRecognize(){
            speechResultFound = false;
            speechRecognizer.startListening(speechIntent);
    }

    public void CancelSpeechRecognizer(){
        speechRecognizer.stopListening();
    }
}
