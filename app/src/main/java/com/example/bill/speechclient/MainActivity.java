package com.example.bill.speechclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import Permission.PermissionActivity;
import Recognize.AssistanListener;
import Recognize.SpeechRegognition;
import TTS.SpeechMessage;
import Utils.ApplicationUtils;
import WitConnection.WitResponse;
import WitConnection.WitResponseMessage;

/**
 * Created by bill on 11/20/17.
 */

@SuppressLint("Registered")
public class MainActivity extends PermissionActivity implements NavigationView.OnNavigationItemSelectedListener, AssistanListener, WitResponseMessage {
    private TextView response;
    private ToggleButton btnIput;
    private ProgressBar progressBar;
    private SpeechRegognition regognition;
    private ProgressBar WaitAction;
    private SpeechMessage talkengine;
    private boolean first;
    private boolean isActivated, detected = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui);
        if (Build.VERSION.SDK_INT < 23) {
            Init();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);


        }
    }

    private void Init() {
        response = (TextView) findViewById(R.id.textView2);
        btnIput = (ToggleButton) findViewById(R.id.toggleButton2);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        response.setText("");
        WaitAction = (ProgressBar) findViewById(R.id.progressBar4);
        regognition = new SpeechRegognition(getApplicationContext());
        regognition.setListener(this);
        regognition.setContinuousSpeechRecognition(true);
        setContinousRecognize();
        setTalkEngine();
        record();

    }

    private void setTalkEngine() {

        talkengine = new SpeechMessage(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    talkengine.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            runThread();

                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {
                            new Thread() {
                                public void run() {
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public synchronized void run() {

                                            regognition.CancelSpeechRecognizer();

                                        }
                                    });

                                }
                            }.start();

                        }
                    });
                } else {
                    Log.e("MainActivity", "Initilization Failed!");
                }
            }
        });
    }

    private void runThread() {

        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (!regognition.isContinuousSpeechRecognition()) {
                            if (first) {
                                first = false;
                                regognition.StartSpeechRegognize();

                            }
                        } else {
                            regognition.StartSpeechRegognize();

                        }
                    }
                });

            }
        }.start();
    }

    private void record() {

        btnIput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                startRecord(b);

            }
        });


    }

    private void startRecord(boolean b) {

        if (b) {
            if (!regognition.isContinuousSpeechRecognition()) {
                first = true;
                isActivated = true;
            } else {
                isActivated = true;
            }

            talkengine.talk("Πείτε μου πως μπορώ να βοηθήσω");
            Toast.makeText(this, "Πείτε μου πως μπορώ να βοηθήσω", Toast.LENGTH_LONG).show();

            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            WaitAction.setIndeterminate(false);
            WaitAction.setVisibility(View.INVISIBLE);
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
            regognition.CancelSpeechRecognizer();

        }

    }

    private void setContinousRecognize() {
        ToggleButton continous = (ToggleButton) findViewById(R.id.buttonContinous);
        continous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    regognition.setContinuousSpeechRecognition(false);
                } else {
                    regognition.setContinuousSpeechRecognition(true);

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_action) {


        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this, SettingsActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void OnSpeechLiveResult(String LiveResult) {
        Log.i("detected", " " + detected + " activated: " + isActivated);
        if (isActivated)
            response.setText(LiveResult);

    }

    @Override
    public void ErrorOnCommand(String msg) {
        if (isActivated)
            startInteraction(msg);

    }

    @Override
    public void ErrorCommand(String msg) {
        if (isActivated)
            startInteraction(msg);

    }

    @Override
    public void OnSpeechResult(String Result) {


        if (isActivated) {
            WaitAction.setIndeterminate(true);
            WaitAction.setVisibility(View.VISIBLE);
            WitConnection.WitResponse witResponse = new WitResponse(this);
            witResponse.execute(Result);

        } else if (Result.equals("Χρύσα")) {
            // regognition.CloseSpeechRegognizer();
            Toast.makeText(this, "Πείτε μου πως μπορώ να βοηθήσω", Toast.LENGTH_LONG).show();

            talkengine.talk("Πείτε μου πως μπορώ να βοηθήσω");
            /*Toast.makeText(this, "Πείτε μου πως μπορώ να βοηθήσω", Toast.LENGTH_LONG).show();*/
            // btnIput.setChecked(true);

            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            isActivated = true;

        }

    }

    @Override
    public void Message(String search, String application, String conf) {
        Log.i("res", search);
        if (search != null) {
            String msg = ApplicationUtils.Selection(application, search, conf, this);
            startInteraction(msg);
        }
    }

    private void startInteraction(String msg) {
        talkengine.talk(msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        response.setText("");
        isActivated = false;
        // btnIput.setChecked(false);

    }

    @Override
    public void OnSpeechError(int Error) {


        isActivated = false;
        response.setText("");
        WaitAction.setIndeterminate(false);
        WaitAction.setVisibility(View.INVISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 7) {
            Init();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);


        }
    }

}
