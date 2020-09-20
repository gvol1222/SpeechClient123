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

public class SpeechRecognition implements RecognitionListener {
    private static final String TAG = "SpeechRecognizer";
    private final SpeechRecognizer speechRecognizer;
    private final Intent speechIntent;
    private final Context context;

    public SpeechRecognition(Context context) {
        this.context = context;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


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

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.e(TAG,"Buffer");
    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        Log.i(TAG, "final results: " + results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
        final String partialResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
        if (!results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0).equals("")) {
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(WitResponse.GetResults(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0)));
        }
        EventBus.getDefault().postSticky(new Events.PartialResults(partialResult));
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
        speechRecognizer.startListening(speechIntent);
    }

    public void CancelSpeechRecognizer(){
        speechRecognizer.stopListening();
    }
}
