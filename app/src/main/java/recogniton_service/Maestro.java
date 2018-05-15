package recogniton_service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.bill.Activities.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import applications.Action;

import applications.Constatns;
import applications.Switcher;
import events.Events;
import utils.jsonparsers.Witobj;

/**
 * Created by bill on 2/18/18.
 */

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class Maestro extends Service {


    public static final String TAG = "Maestro";
    private int RETRY_FLAG = 0;
    private int RETRY_LIMIT = 5;
    private Action app;
    private Witobj resp;


    public Maestro() {


    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Subscribe
    public void getWitResponse(Events.WitREsp event){
        String sender1 = event.getSender();

        // Log.d(TAG, "the response url from wit is "+sender1);


        //Button Press
///        if(sender1.equals("BTN")){
        // Retry flag initialization for the NULL loop of WIT
        //   RETRY_FLAG = 0;
        //app = new Action();

        //Wit Object Response
        if(sender1.equals("WIT"))
        {

            resp = event.getWitResponse();

            app = Constatns.app;


            Log.d(TAG, "App stage is"+app.type+" "+RETRY_FLAG);
            //IF response = null
            //Retry to catch user command - ends after RETRY_LIMIT
            if(resp.getEntities().getIntent() == null && app.Stage.equals(Constatns.IN_STAGE)){

                app.Stage = Constatns.NO_SPEACH_STAGE;
                Log.d(TAG, "no speech ");
           /* if (RETRY_FLAG < RETRY_LIMIT){
                speak(getString(R.string.command_repeat),true);
            }
            else {
                speak(getString(R.string.retry_silent_stop),false);
            }
            RETRY_FLAG = RETRY_FLAG + 1;*/
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
                    Log.d("current key smsm",app.Current_Key+" "+resp.getEntities().getNumber().get(0).getValue());
                    app.data.put(app.Current_Key,resp.getEntities().getNumber().get(0).getValue());

                }

                //One time no multistage comminicators to pass data from phone number
                if(resp.getEntities().getPhoneNumber() !=null && resp.getEntities().getPhoneNumber().get(0).getConfidence() >0.8 ) {
                    app.data.put(app.Current_Key,resp.getEntities().getPhoneNumber().get(0).getValue());
                }

                if(resp.getEntities().getDatetime() !=null && resp.getEntities().getDatetime().get(0).getConfidence() >0.8 ) {
                    app.data.put(Constatns.REM_KEY_TIME,resp.getEntities().getDatetime().get(0).getValue());
                    Log.i(TAG,"rem key time = "+resp.getEntities().getDatetime().get(0).getValue());
                }
                if(resp.getEntities().getDuration() !=null && resp.getEntities().getDuration().get(0).getConfidence() >0.8 ) {
                    app.data.put(Constatns.TIMER_KEY,resp.getEntities().getDuration().get(0).getNormalized().getValue());
                    Log.i(TAG,"rem duration= "+resp.getEntities().getDuration().get(0).getNormalized().getValue());
                }


                //Multi stage comm gatherer
                if(resp.getText() != null && app.waiting_data && app.data.get(Constatns.TIMER_KEY)==null && !(resp.getEntities().getDatetime() !=null
                        && resp.getEntities().getDatetime().get(0).getConfidence() >0.8 ) ){
                    app.data.put(app.Current_Key,resp.getText());
                    Log.i(TAG,"multistage data response from user is = "+resp.getText());
                }else if(app.waiting_data && (resp.getEntities().getDatetime() !=null &&  app.data.get(Constatns.TIMER_KEY)==null
                        && resp.getEntities().getDatetime().get(0).getConfidence() >0.8 ) ){
                    app.data.put(Constatns.REM_KEY_TIME,resp.getEntities().getDatetime().get(0).getValue());
                }


                //Multi Stage comm Loop
                if(app.data.containsValue(null)){
                    for (String key : app.data.keySet()) {
                        Log.i(TAG,"multistage data in comm loop = "+app.data.keySet());

                        if (app.data.get(key) == null) {
                            Log.i(TAG," key "+key);
                            speak(app.data_requests.get(key), true);
                            app.Current_Key = key;
                            app.waiting_data = true;
                            break;
                        }
                    }
                }else {
                    app.Stage = Constatns.TR_STAGE;

                }



            }

            if (app.Stage.equals(Constatns.TR_STAGE)){
                Log.i(TAG," entered in tr stage = "+app.data.get(app.Current_Key));
                app.waiting_data = false;
                app = Switcher.transforminfo(app,getApplicationContext());

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
                    EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
                    speak("όπως επιθυμείτε", false);
                }
                Log.i(TAG,"entered in after vr stage= ");
            }


            if (app.Stage.equals(Constatns.RUN_STAGE)){
                app.runIntent(getApplicationContext());
                //app.Stage = Constatns.CP_STAGE;
                EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
                Log.i(TAG,"entered in run stage");
            }

            if (app.Stage.equals(Constatns.NF_STAGE)){
                EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
                speak(app.NOT_FOUND,false);
                Log.i(TAG,"entered in not found stage");
                //app.Stage = Constatns.CP_STAGE;

            }
            /**/ if (app.Stage.equals(Constatns.CP_STAGE)){
            Log.i(TAG,"completed"+app.LAUNCHED);

            if(!app.LAUNCHED.equals(""))

                speak(app.LAUNCHED,false);
                Constatns.app.Init();
            Log.i(TAG,"entered in completed stage");
        }



        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void speak(String msg, boolean recognizeAfter){
        Log.i(TAG,"entered in speak method"+msg+" "+recognizeAfter);

        if(!msg.equals(""))
            EventBus.getDefault().post(new Events.SpeechMessage(msg,recognizeAfter));

    }
}