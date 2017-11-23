package com.example.bill.speechclient;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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

public abstract class AssistanActivity extends PermissionActivity implements AssistanListener, WitResponseMessage {

    private SpeechRegognition regognition;
    private SpeechMessage talkengine;
    private boolean first;
    private boolean isActivated, detected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 23)
            Init();
    }

    private void Init() {

        regognition = new SpeechRegognition(getApplicationContext());
        regognition.setListener(this);
        regognition.setContinuousSpeechRecognition(true);
        setTalkEngine();

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


    public void startRecord(boolean b) {

        if (b) {
            if (!regognition.isContinuousSpeechRecognition()) {
                first = true;
                isActivated = true;
            } else {
                isActivated = true;
            }

            talkengine.talk("Πείτε μου πως μπορώ να βοηθήσω");
            Toast.makeText(this, "Πείτε μου πως μπορώ να βοηθήσω", Toast.LENGTH_LONG).show();


        } else {

            regognition.CancelSpeechRecognizer();

        }

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
            WitConnection.WitResponse witResponse = new WitResponse(this);
            witResponse.execute(Result);

        } else if (Result.equals("Χρύσα")) {

            Toast.makeText(this, "Πείτε μου πως μπορώ να βοηθήσω", Toast.LENGTH_LONG).show();
            talkengine.talk("Πείτε μου πως μπορώ να βοηθήσω");

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
        isActivated = false;

    }

    @Override
    public void OnSpeechError(int Error) {
        isActivated = false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 7) {
            Init();
        }
    }


}
