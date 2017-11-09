package com.example.bill.speechclient;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import Recognize.AssistanListener;
import Recognize.SpeechRegognition;
import WitConnection.WitResponse;

public class MainActivity extends Activity implements AssistanListener {


    private static final int RequestPermissionCode = 7;
    private WitResponse WitResponse;
    private TextView response;
    private ToggleButton btnIput;
    private ProgressBar progressBar;
    private SpeechRegognition regognition;
    private ProgressBar WaitAction;

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
        response.setText("Αναμένω εντολή");
        WaitAction = (ProgressBar) findViewById(R.id.progressBar2);

        regognition = new SpeechRegognition(getApplicationContext());
        regognition.setListener(this);

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
            regognition.StartSpeechRegognize();

        }else {
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
            regognition.CloseSpeechRegognizer();

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

    @Override
    public void OnSpeechLiveResult(String LiveResult) {
        response.setText(LiveResult);

    }

    @Override
    public void OnSpeechResult(String Result) {
        WitResponse = new WitResponse(MainActivity.this);
        WitResponse.execute(Result);

    }

    @Override
    public void OnSpeechError(String Error) {

    }

}

