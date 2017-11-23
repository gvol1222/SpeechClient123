package com.example.bill.speechclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import TTS.SpeecHelper;
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
    private ProgressBar WaitAction;
    private ToggleButton continous;
    private Toolbar toolbar;

    private boolean isActivated;

    private SpeecHelper talkengine;
    private SpeechRegognition recognition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui);
        if (Build.VERSION.SDK_INT < 23) Init();
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

    private void Init() {
        setRecognition();
        talkengine = new SpeecHelper(this, recognition);
        setButtons();
        setProgress();
        setText();
        setToolbar();
        setDrawerLayout();
        setNavigation();
        setContinousRecognize();
        record();
    }

    //set gui functions
    private void setNavigation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setDrawerLayout() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setButtons() {
        btnIput = (ToggleButton) findViewById(R.id.toggleButton2);
        continous = (ToggleButton) findViewById(R.id.buttonContinous);
    }

    private void setProgress() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        WaitAction = (ProgressBar) findViewById(R.id.progressBar4);
    }

    private void setText() {
        response = (TextView) findViewById(R.id.textView2);
        response.setText("");
    }

    //set recognition
    private void setRecognition() {
        recognition = new SpeechRegognition(getApplicationContext());
        recognition.setListener(this);
        recognition.setContinuousSpeechRecognition(true);
    }

    private void record() {

        btnIput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                startRecord(b);

            }
        });


    }

    private void clearProgressBar() {
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void clearWaitBar() {
        WaitAction.setIndeterminate(false);
        WaitAction.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showWaitBar() {
        WaitAction.setIndeterminate(true);
        WaitAction.setVisibility(View.VISIBLE);
    }

    private void startRecord(boolean b) {

        if (b) {
            if (!recognition.isContinuousSpeechRecognition()) {
                talkengine.setFirst(true);
                isActivated = true;
            } else {
                isActivated = true;
            }
            talkengine.speak("Πείτε μου πως μπορώ να βοηθήσω");
            Toast.makeText(this, "Πείτε μου πως μπορώ να βοηθήσω", Toast.LENGTH_LONG).show();
            showProgressBar();
        } else {
            clearProgressBar();
            clearWaitBar();
            recognition.CancelSpeechRecognizer();
        }

    }

    private void setContinousRecognize() {
        continous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    recognition.setContinuousSpeechRecognition(false);
                } else {
                    recognition.setContinuousSpeechRecognition(true);

                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (recognition != null) {
            recognition.CloseSpeechRegognizer();
            recognition = null;
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
            showWaitBar();
            WitConnection.WitResponse witResponse = new WitResponse(this);
            witResponse.execute(Result);

        } else if (Result.equals("Χρύσα")) {
            Toast.makeText(this, "Πείτε μου πως μπορώ να βοηθήσω", Toast.LENGTH_LONG).show();
            talkengine.speak("Πείτε μου πως μπορώ να βοηθήσω");
            showProgressBar();
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
        talkengine.speak(msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        response.setText("");
        clearWaitBar();
        if (!recognition.isContinuousSpeechRecognition()) {
            btnIput.setChecked(false);
        }

        isActivated = false;
    }

    @Override
    public void OnSpeechError(int Error) {
        isActivated = false;
        response.setText("");
        clearProgressBar();
        clearWaitBar();
        if (!recognition.isContinuousSpeechRecognition()) {
            btnIput.setChecked(false);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 7) {
            Init();
        }
    }

}
