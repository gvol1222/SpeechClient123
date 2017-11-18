package com.example.bill.speechclient;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import TTS.SpeechMessage;
import Utils.ApplicationUtils;
import WitConnection.WitResponse;
import WitConnection.WitResponseMessage;

public class MainActivity extends Activity implements AssistanListener, WitResponseMessage {


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
            SpeechMessage.Talk(this, "Περιμένω εκφώνηση εντολής");
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    regognition.StartSpeechRegognize();
                }
            }, 3000);

        }else {
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
            regognition.CloseSpeechRegognizer();

        }

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (regognition != null) {
            regognition.CloseSpeechRegognizer();
            regognition = null;
        }
        SpeechMessage.cancel();
        super.onDestroy();


    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT < 23) {
            Init();
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void ErrorOnCommand(String msg) {
        regognition.CloseSpeechRegognizer();
        SpeechMessage.Talk(this, msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                regognition.StartSpeechRegognize();
            }
        }, 3000);

    }

    @Override
    public void ErrorCommand(String msg) {
        regognition.CloseSpeechRegognizer();
        SpeechMessage.Talk(this, msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                regognition.StartSpeechRegognize();
            }
        }, 3000);

    }

    @Override
    public void Message(String search, String application, String conf) {

        if (search != null) {
            String msg = ApplicationUtils.Selection(application, search, conf, this);

            regognition.CloseSpeechRegognizer();
            SpeechMessage.cancel();
            SpeechMessage.Talk(this, msg);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    regognition.StartSpeechRegognize();
                }
            }, 3000);
        }
    }
}