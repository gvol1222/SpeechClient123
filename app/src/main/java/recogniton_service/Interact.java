package recogniton_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.bill.Activities.R;

import applications.Action;
import applications.AppIntentService;
import applications.CallTel;
import applications.Constatns;
import applications.Switcher;
import recognize.Constants;
import utils.jsonparsers.Witobj;

/**
 * Created by bill on 12/12/17.
 */

public abstract class Interact extends SpeechService {

    private final String TAG = "Interact";
    private int RETRY_FLAG = 0;
    private int RETRY_LIMIT = 5;
    private String result ="";
    private boolean HAS_APP = false;
    private Action app;
    boolean second =false;

    final static public String MaestroComm = "Maestro Communication Channell";
    @Override
    public void onCreate() {
        super.onCreate();

        app = new Action();
        IntentFilter iff= new IntentFilter(MaestroComm);
        LocalBroadcastManager.getInstance(this).registerReceiver(MaestroReceiver,iff);
    }
    private void CommandReceived(Intent intent){
        String sender1 = intent.getStringExtra("Sender");
        Log.d(TAG, "the response url from wit is "+sender1);


        //Button Press
        if(sender1.equals("BTN")){
            // Retry flag initialization for the NULL loop of WIT
            RETRY_FLAG = 0;
            //app = new Action();
            speak(getString(R.string.helper_prompt),true);
        }

        //Wit Object Response
        if(sender1.equals("WIT")){

            Witobj resp = (Witobj) intent.getSerializableExtra("WitOBJ");

            Log.d(TAG, "App stage is"+app.Stage);
            //IF response = null
            //Retry to catch user command - ends after RETRY_LIMIT
            if(resp.getEntities().getIntent() == null && app.Stage.equals(Constatns.IN_STAGE)){

                app.Stage = Constatns.NO_SPEACH_STAGE;
                Log.d(TAG, "no speech ");
                if (RETRY_FLAG < RETRY_LIMIT){
                    speak(getString(R.string.command_repeat),true);
                }
                else {
                    speak(getString(R.string.retry_silent_stop),false);
                }
                RETRY_FLAG = RETRY_FLAG + 1;
            }

            if(resp.getEntities()!=null){
                app.entities = resp.getEntities();
            }

            //Initialization Phase
            if (app.Stage.equals(Constatns.IN_STAGE)){
                String type = resp.getEntities().getIntent().get(0).getValue();
                app = Switcher.selectActionbyType(app,type);
                Log.d(TAG, "entered in init stage");
            }

            //Data Fill Phase
            if (app.Stage.equals(Constatns.CH_STAGE)){
                Log.i(TAG,"entered in data fill stage ");

                //One time no multistage comminicators to pass data from appdata
                if( resp.getEntities().getAppData() != null && resp.getEntities().getAppData().get(0).getConfidence()> 0.8) {
                    app.data.put(app.Current_Key,resp.getEntities().getAppData().get(0).getValue());
                }

                //One time no multistage comminicators to pass data from number
                if( resp.getEntities().getNumber() !=null &&  resp.getEntities().getNumber().get(0).getConfidence() > 0.8  )
                {
                    app.data.put(app.Current_Key,resp.getEntities().getNumber().get(0).getValue());
                }

                //One time no multistage comminicators to pass data from phone number
                if(resp.getEntities().getPhoneNumber() !=null && resp.getEntities().getPhoneNumber().get(0).getConfidence() >0.8 ) {
                    app.data.put(app.Current_Key,resp.getEntities().getPhoneNumber().get(0).getValue());
                }

                //Multi stage comm gatherer
                if(resp.getText() != null && app.waiting_data){
                    app.data.put(app.Current_Key,resp.getText());
                    Log.i(TAG,"multistage data response from user is = "+resp.getText());
                }

                //Multi Stage comm Loop
                for (String key : app.data.keySet()) {
                    Log.i(TAG,"multistage data in comm loop = "+app.data.get(key));

                    if (app.data.get(key) == null) {

                        speak(app.data_requests.get(key), true);
                        app.Current_Key = key;
                        app.waiting_data = true;
                        break;
                    } else {
                            app.Stage = Constatns.TR_STAGE;

                    }
                }


            }


            if (app.Stage.equals(Constatns.TR_STAGE)){
                Log.i(TAG," entered in tr stage = ");
                app.waiting_data = false;
                app = Switcher.transforminfo(app,getApplicationContext());
            }

            if (app.Stage.equals(Constatns.MULTI_COMMAND_FROM_START)){

                Log.i(TAG,"entered in multi command stage from start");

                if(resp.getText() != null  && app.waiting_data){
                    app.data.put(app.Current_Key,resp.getText());
                    app.waiting_data =false;
                }

                if (app.data.get(app.Current_Key) ==null) {
                    speak(app.data_requests.get(app.Current_Key), true);
                    app.waiting_data = true;
                } else {
                    app.Stage = Constatns.VR_STAGE;
                }
            }

            if (app.Stage.equals(Constatns.VR_STAGE)){

                speak(app.VERIFY_MESSAGE,true);
                app.Stage = Constatns.AFTER_VR_STAGE;
                Log.i(TAG,"entered in vr stage= ");
            }

            if (app.Stage.equals(Constatns.AFTER_VR_STAGE)){

                if(resp.getText().contains("ναι")) {
                    app.Stage = Constatns.RUN_STAGE;
                }
                else if(resp.getText().contains("όχι") ) {
                    app.Stage = Constatns.CP_STAGE;
                    speak("όπως επιθυμείτε", false);
                }
                Log.i(TAG,"entered in after vr stage= ");
            }

            if (app.Stage.equals(Constatns.RUN_STAGE)){
                app.runIntent(getApplicationContext());
                app.Stage = Constatns.CP_STAGE;
                Log.i(TAG,"entered in run stage");
            }

            if (app.Stage.equals(Constatns.NF_STAGE)){

                speak(app.NOT_FOUND,false);
                Log.i(TAG,"entered in not found stage");
                app.Stage = Constatns.CP_STAGE;

            }
            if (app.Stage.equals(Constatns.CP_STAGE)){
                Log.i(TAG,"completed");
                app.Init();
                setActivated(false);
                SendMessage("");
                Log.i(TAG,"entered in completed stage");
            }
            if(app.Stage.equals("NS")){
                app.Stage=Constatns.IN_STAGE;
                app.Init();
                SendMessage("");
                Log.i(TAG,"entered in no speech stage= ");
            }


        }
    }
    private BroadcastReceiver MaestroReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CommandReceived(intent);
        }
    };
    private void speak (String message,boolean recognize_after){
        //Intent msg = new Intent();

        if (recognize_after)
            setActivated(true);
        else
            setActivated(false);

        StartMessage(message);
    }




}