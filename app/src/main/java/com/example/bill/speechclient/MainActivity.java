package com.example.bill.speechclient;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Locale;
import java.util.jar.Manifest;

public class MainActivity extends Activity implements RecognitionListener {



    private TextView response;
    private ToggleButton btnIput;
    private ProgressBar progressBar;
    private SpeechRecognizer speechRecognizer;
    private Intent intent;
    private static final String adress ="https://api.wit.ai/message?v=20171023&q=";

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestAudioPermissions();
    }


    private void Init(){
        response = (TextView) findViewById(R.id.txtResponse);
        btnIput = (ToggleButton) findViewById(R.id.toggleButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,3);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,10000);
        record();
    }

    private void record(){

        btnIput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                startRecord(b);
            }
        });

    }


    private void startRecord(boolean b){

        if(b){
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            speechRecognizer.startListening(intent);
        }else {
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
            speechRecognizer.stopListening();

        }

    }



    @Override
    protected void onPause() {
        super.onPause();
        if(speechRecognizer != null)
            speechRecognizer.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);

    }

    @Override
    public void onRmsChanged(float v) {
        progressBar.setProgress((int) v);
    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        response.setText(matches.get(0));
        CallWit callWit = new CallWit();
        callWit.execute(adress,matches.get(0));

    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }


    private void requestAudioPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            Init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case MY_PERMISSIONS_RECORD_AUDIO:{

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    Init();
                }else {
                    Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}
