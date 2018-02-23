package recogniton_service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.bill.Activities.R;

import applications.Action;

import applications.Constatns;
import applications.Switcher;
import utils.jsonparsers.Witobj;

/**
 * Created by bill on 2/18/18.
 */

public class Maestro extends IntentService {


    public static final String TAG = "Maestro";
    private int RETRY_FLAG = 0;
    private int RETRY_LIMIT = 5;
    private Action app;
    public Maestro() {
        super("Maestro");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

            CommandReceived(intent);
    }

    private void CommandReceived(Intent intent){
        String sender1 = intent.getStringExtra("Sender");
        app = Constatns.app;
        Log.d(TAG, "the response url from wit is "+sender1);


        //Button Press
        if(sender1.equals("BTN")){
            // Retry flag initialization for the NULL loop of WIT
            RETRY_FLAG = 0;
            //app = new Action();
            speak(getString(R.string.helper_prompt),true);
        }

        //Wit Object Response
        if(sender1.equals("WIT"))
        {

            Witobj resp = (Witobj) intent.getSerializableExtra("WitOBJ");

            Log.d(TAG, "App stage is"+app.type);
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
                Log.d(TAG, "type = "+resp.getEntities().getIntent().get(0).getValue());
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

                if(resp.getEntities().getDatetime() !=null && resp.getEntities().getDatetime().get(0).getConfidence() >0.8 ) {
                    app.data.put(Constatns.REM_KEY_TIME,resp.getEntities().getDatetime().get(0).getValue());
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

                for (String key : app.data_requests.keySet()) {

                    if (app.data_requests.get(key) != null) {
                        speak(app.data_requests.get(app.Current_Key), true);
                        app.Current_Key = key;
                        app.waiting_data = true;
                        app.data_requests.remove(key);
                        break;
                    }

                }
                if (app.data_requests.size() == 0) {
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
                    // app.Stage = Constatns.CP_STAGE;
                    speak("όπως επιθυμείτε", false);
                }
                Log.i(TAG,"entered in after vr stage= ");
            }


            if (app.Stage.equals(Constatns.RUN_STAGE)){
                app.runIntent(getApplicationContext());
                //app.Stage = Constatns.CP_STAGE;
                Log.i(TAG,"entered in run stage");
            }

            if (app.Stage.equals(Constatns.NF_STAGE)){

                speak(app.NOT_FOUND,false);
                Log.i(TAG,"entered in not found stage");
                //app.Stage = Constatns.CP_STAGE;

            }
           /* if (app.Stage.equals(Constatns.CP_STAGE)){
                Log.i(TAG,"completed");
                //app.Init();
                setActivated(false);
                Log.i(TAG,"entered in completed stage");
            }*/



        }
    }

private void speak(String msg,boolean recognizeAfter){
    Log.i(TAG,"entered in speak method"+msg+" "+recognizeAfter);
    Intent broadcastIntent = new Intent(Constatns.MAESTRO_ACTION);
    broadcastIntent.putExtra("msg",msg);
    broadcastIntent.putExtra("speak","speak");
    broadcastIntent.putExtra("rec",recognizeAfter);
    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
    localBroadcastManager.sendBroadcast(broadcastIntent);

}
}
