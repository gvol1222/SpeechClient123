package com.example.bill.speechclient;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;

import Applications.CallTel;
import Applications.youtube;
import Recognize.AssistanListener;
import Recognize.SpeechRegognition;
import Utils.ApplicationUtils;
import WitConnection.WitResponse;

public class MainActivity extends Activity implements AssistanListener {


    private static final int RequestPermissionCode = 7;
    private WitResponse WitResponse;
    private TextView response;
    private ToggleButton btnIput;
    private ProgressBar progressBar;
    private SpeechRegognition regognition;
    private youtube YouT;
    private CallTel callTel;
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
       /* YouT = new youtube(Intent.ACTION_SEARCH, this.getApplicationContext());
        YouT.SetData("com.google.android.youtube");
        YouT.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        callTel = new CallTel(Intent.ACTION_CALL, this.getApplicationContext());
        callTel.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        */
        regognition = new SpeechRegognition(this);
        regognition.setListener(this);

        record();

    }

    private void CreateResponse() {
        WitResponse = new WitResponse() {
            @Override
            protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
                super.onPostExecute(stringStringHashMap);

                Log.d("APPKind", String.valueOf(stringStringHashMap.entrySet()));

                String application = stringStringHashMap.get("Action");

                String search = stringStringHashMap.get("App_data");
                Log.d("APPKind", application);
                ApplicationUtils.Selection(application, search, getApplicationContext());
               /* if (application == null){
                    response.setText("Λάθος Εντολή");
                    cancel(true);
                    WaitAction.setIndeterminate(false);
                    WaitAction.setVisibility(View.INVISIBLE);
                }
                switch (application)
                {
                    case "play_video":
                        YouT.AddExtra("query", search);
                        YouT.TriggerIntent();
                        WaitAction.setIndeterminate(false);
                        WaitAction.setVisibility(View.INVISIBLE);
                        break;
                    case "make_call":
                        Log.i(TAG, "Name: " + search);
                              ArrayList<String> tel = ContactUtils.ContactNumber(getApplicationContext(),search);
                              callTel.setData(Uri.parse("tel:" + tel.get(0)));
                            WaitAction.setIndeterminate(false);
                            WaitAction.setVisibility(View.INVISIBLE);
                            callTel.TriggerIntent();

                        break;

                    default:
                        break;
                }*/
            }
        };
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
        CreateResponse();
        WitResponse.execute(Result);

    }

    @Override
    public void OnSpeechError(String Error) {

    }

}

