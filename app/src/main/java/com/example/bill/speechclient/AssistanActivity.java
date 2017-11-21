package com.example.bill.speechclient;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import Permission.PermissionActivity;
import Recognize.AssistanListener;
import Recognize.Constants;
import Recognize.SpeechRegognition;
import TTS.SpeechMessage;
import Utils.ApplicationUtils;
import WitConnection.WitResponse;
import WitConnection.WitResponseMessage;

/**
 * Created by bill on 11/20/17.
 */

public abstract class AssistanActivity extends PermissionActivity implements AssistanListener, WitResponseMessage {

    private TextView response;
    private ToggleButton btnIput;
    private ProgressBar progressBar;
    private SpeechRegognition regognition;
    private ProgressBar WaitAction;
    private SpeechMessage talkengine;
    private boolean first;
    private boolean isActivated;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 23)
            Init();
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
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
            regognition.CloseSpeechRegognizer();

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
            isActivated = true;
            regognition.CloseSpeechRegognizer();
            talkengine.talk("Πείτε μου πως μπορώ να βοηθήσω");
            Toast.makeText(this, "Πείτε μου πως μπορώ να βοηθήσω", Toast.LENGTH_LONG).show();
            //   btnIput.setChecked(true);

            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
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
        regognition.CloseSpeechRegognizer();
        talkengine.talk(msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        response.setText("");
        isActivated = false;
        btnIput.setChecked(false);
        WaitAction.setIndeterminate(false);
        WaitAction.setVisibility(View.INVISIBLE);
    }

    @Override
    public void OnSpeechError(int Error) {
        if ((Constants.ErrorNoMatch == Error || Constants.ErrorSpeechTimeOut == Error || Constants.ErrorAudio == Error)) {
            isActivated = false;
            response.setText("");
            btnIput.setChecked(false);
            WaitAction.setIndeterminate(false);
            WaitAction.setVisibility(View.INVISIBLE);
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
