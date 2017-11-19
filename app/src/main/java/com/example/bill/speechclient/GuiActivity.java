package com.example.bill.speechclient;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class GuiActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AssistanListener, WitResponseMessage {

    private static final int RequestPermissionCode = 7;
    private WitResponse WitResponse;
    private TextView response;
    private ToggleButton btnIput;
    private ProgressBar progressBar;
    private SpeechRegognition regognition;
    private ProgressBar WaitAction;
    private SpeechMessage talkengine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        requestPermissions();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_action) {


        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Init(){
        response = (TextView) findViewById(R.id.textView2);
        btnIput = (ToggleButton) findViewById(R.id.toggleButton2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        response.setText("Αναμένω εντολή");
        WaitAction = (ProgressBar) findViewById(R.id.progressBar2);
        regognition = new SpeechRegognition(getApplicationContext());
        regognition.setListener(this);
        talkengine = new SpeechMessage(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    talkengine.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                        regognition.StartSpeechRegognize();
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {
                        }
                    });
                } else {
                    Log.e("MainActivity", "Initilization Failed!");
                }
            }
        });


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
            talkengine.talk("Πείτε μου πως μπορώ να βοηθήσω");
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
        talkengine.cancel();
        super.onDestroy();


    }

    @Override
    public void OnSpeechLiveResult(String LiveResult) {
        response.setText(LiveResult);

    }

    @Override
    public void OnSpeechResult(String Result) {
        WitResponse = new WitResponse(GuiActivity.this);
        WitResponse.execute(Result);

    }

    @Override
    public void OnSpeechError(String Error) {

    }

    @Override
    public void ErrorOnCommand(String msg) {
        regognition.CloseSpeechRegognizer();
        talkengine.talk(msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    }

    @Override
    public void ErrorCommand(String msg) {
        regognition.CloseSpeechRegognizer();
        talkengine.talk(msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    }

    @Override
    public void Message(String search, String application, String conf) {

        if (search != null) {
            String msg = ApplicationUtils.Selection(application, search, conf, this);

            regognition.CloseSpeechRegognizer();

            talkengine.talk(msg);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
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
}
