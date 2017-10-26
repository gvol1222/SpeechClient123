package com.example.bill.speechclient;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity implements RecognitionListener {


    private static final String adress = "https://api.wit.ai/message?v=20171023&q=";
    private static final int RequestPermissionCode = 7;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private CallWit callWit;
    private TextView response;
    private ToggleButton btnIput;
    private ProgressBar progressBar;
    private SpeechRecognizer speechRecognizer;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();



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
            Log.d("changed", String.valueOf(b));

        }else {
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
            speechRecognizer.stopListening();

        }

    }



    @Override
    protected void onPause() {
        super.onPause();
        //if(speechRecognizer != null)
        //speechRecognizer.destroy();
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
        Log.d("starting....","test");

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
        btnIput.setChecked(false);

        // speechRecognizer.stopListening();
    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        response.setText(matches.get(0));
        callWit = new CallWit(this.getApplicationContext(), this);
        callWit.execute(adress,matches.get(0));

    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }


    private void requestPermissions() {

        String[] permissions = new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECORD_AUDIO
        };

        ActivityCompat.requestPermissions(this, permissions, RequestPermissionCode);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case RequestPermissionCode:

                if (grantResults.length > 0) {
                    boolean ReadContacts = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean CallPhone = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordAudio = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (ReadContacts && CallPhone && RecordAudio) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                        Init();
                    } else {
                        Toast.makeText(this, "Permission Deinied", Toast.LENGTH_LONG).show();
                    }

                }
                break;
        }
    }
}

