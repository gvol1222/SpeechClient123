package recogniton_service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.bill.Activities.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import applications.Constatns;
import events.Events;
import recognize.SpeechRegognition;
import tts.SpeecHelper;
import tts.TtsProgressListener;
import wit_connection.WitResponse;

/**
 * Created by bill on 11/30/17.
 */

//this class contains functions of speech recognizer
public abstract class RecognitionService extends Service implements  TtsProgressListener {

    private final String TAG = this.getClass().getSimpleName();
    private SpeechRegognition recognition;
    private Handler startHandler;
    private Handler closeHandler;
    private boolean isFirst;
    private SpeecHelper talkengine;
    private String startMessage = "";
    private String waitMessage = "";
    private boolean isActivated;
    private boolean isFinishedTts;
    private Handler mHandler;
    // Intent broadcastIntent;


    /**/@Subscribe
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void OnSpeechError(Events.SpeechError event){

        if(event.isError()){
            EventBus.getDefault().post(new Events.PartialResults(""));
            if (isActivated()) {
                //app.Stage=Constatns.NO_SPEACH_STAGE;
                Toast.makeText(this, "Η αναγνώριση τερματίζει", Toast.LENGTH_SHORT).show();
            }
            setActivated(false);
            //close recognition if not continuous
            CancelOnNotContinuous();
            //mute audio beep
            Mute(true);
            Constatns.app.Init();
        }

    }
    @Subscribe
    public void OnSpeechResult(Events.FinalResults event){

        String Result = event.getFinalResults();

        Log.i(TAG, "On partial res speech"+Result);
        if (isActivated() && !Result.equals("")) {
            //EventBus.getDefault().postSticky(new PartialSpeechText(""));
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(WitResponse.GetResults(Result));
        } else if (Result.equals(getResources().getString(R.string.title_activity_gui))) {

            Mute(false);
            StartMessage(getApplicationContext().getResources().getString(R.string.StartMessage));
            setActivated(true);
        }

    }
    @Subscribe
    public void OnSpeechMessage(Events.SpeechMessage event){


        boolean reocgnizeAfter = event.getRecognige_after();
        String message = event.getSpeechMessage();
        speak(message,reocgnizeAfter);
      //  EventBus.getDefault().postSticky(new SpeechMessage("",false));


    }
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        //EventBus.getDefault().register(this);
       // broadcastIntent = new Intent(BroadcastAction);
        InitHandler();
        Init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       //EventBus.getDefault().unregister(this);
        free();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //EventBus.getDefault().unregister(this);
       // free();
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onStartTalk() {
        //on start talking assistant close recognition and enable beep
        isFinishedTts =false;

        Mute(false);
        runCloseSpeech();
    }


    @Override
    public void onEndTalk() {
        //on end talking assistant start recognition
      //  broadcastIntent.putExtra("ripple", "ripple");
       // sendBroadcast(broadcastIntent);
        isFinishedTts =true;
        runStartSpeech();

    }
    //set speech recognition for continuous recognition o not continuous
    public void setContinuous(boolean continuous) {
        Log.i(TAG, "continuous parameter is " + continuous);
        recognition.setContinuousSpeechRecognition(continuous);

    }
    private void Init() {
        Log.i(TAG, "Initialization object and messages");
        isFinishedTts =true;
        talkengine = new SpeecHelper(getApplicationContext(), this);
        startMessage = getApplicationContext().getResources().getString(R.string.StartMessage);
        waitMessage = getApplicationContext().getResources().getString(R.string.WaitMessage);
        setRecognition();
    }
    //close and destroy speech recognition
    private void free() {
        Log.i(TAG, "Free resources");
        if (recognition != null) {
            recognition.CloseSpeechRegognizer();
            recognition = null;
        }
        if (talkengine != null)
            talkengine.cancel();

    }

    public void StartMessage(final String msg) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RecognitionService.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        talkengine.speak(msg);



    }

    //initiate handler for running on main thread
    private void InitHandler() {
        Log.i(TAG, "Initialization of handlers");
        startHandler = new Handler(Looper.getMainLooper());
        closeHandler = new Handler(Looper.getMainLooper());
    }

    public void setRecognition() {
        Log.i(TAG, "Recognition created");
        recognition = new SpeechRegognition(getApplicationContext());

    }

    public void CancelOnNotContinuous() {
        if (!recognition.isContinuousSpeechRecognition()) {
            StopSrecognition();
        }
    }

    public void StopSrecognition() {
        if (recognition != null)
            recognition.CancelSpeechRecognizer();


    }

    public void StartRecognition() {
        if (recognition != null) {
            startService(new Intent(this, Maestro.class));
            recognition.StartSpeechRegognize();
        }
    }

    //mute and unmute beep sound
    public void Mute(Boolean mute) {
        recognition.MuteAudio(mute);

    }

    //this functions that start and close the speech recognition must run on the main thread
    public void runCloseSpeech() {
        closeHandler.post(new Runnable() {
            @Override
            public void run() {
                StopSrecognition();
                Log.i(TAG, "recognition closed");
            }
        });
    }

    public void runStartSpeech() {

        startHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "recognition started first: " + isFirst);
                if (!recognition.isContinuousSpeechRecognition()) {
                    //if (isFirst) {
                       // isFirst = false;
                        StartRecognition();
                  //  }
                } else {
                    StartRecognition();
                }
            }
        });
    }

    protected boolean isContinuousSpeechRecognition() {
        return recognition.isContinuousSpeechRecognition();
    }

    //this function is usefull for not continuous recognition
    public void setFirst(boolean first) {
        Log.i(TAG, "first parameter is " + first);
        isFirst = first;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        Log.i(TAG,"boolean activated is "+activated);
        isActivated = activated;

    }

    public boolean isFinishedTts() {
        return isFinishedTts;
    }

    public void setFinishedTts(boolean finishedTts) {
        isFinishedTts = finishedTts;
    }

    //function for starting tts speaking
    public void StartInteract() {
        Log.i(TAG, "Assistant starting speaking");
        isActivated = true;

        StartMessage(startMessage);
       // if (!isContinuousSpeechRecognition())
          //  setFirst(true);
    }
    private void speak (String message,boolean recognize_after){
        //Intent msg = new Intent();

        if (recognize_after)
            setActivated(true);
        else
            setActivated(false);

        StartMessage(message);
    }


}
