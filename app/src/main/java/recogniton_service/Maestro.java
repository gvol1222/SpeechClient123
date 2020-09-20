package recogniton_service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import applications.Action;

import applications.Constants;
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

            app = Constants.app;


            Log.d(TAG, "App stage is"+resp.getIntents().size()+" ");
            //IF response = null
            //Retry to catch user command - ends after RETRY_LIMIT
            if((resp.getIntents()== null || resp.getIntents().size() ==0) && app.Stage.equals(Constants.IN_STAGE)){
                speak("Παρακαλώ επαναλάβετε",true);
                app.Stage = Constants.NO_SPEACH_STAGE;
                Log.d(TAG, "no speech "+RETRY_FLAG);
            if (RETRY_FLAG < RETRY_LIMIT){
                app.Stage= Constants.IN_STAGE;
                speak("Παρακαλώ επαναλάβετε",true);
                RETRY_FLAG = RETRY_FLAG + 1;
            }
            else {
                RETRY_FLAG = 0;
                speak("Παρακαλώ προσπαθήστε ξανά",false);
            }
                Log.d(TAG, "no speech "+RETRY_FLAG);

            }/**/

            if(resp.getEntities()!=null){
                app.entities = resp.getEntities();
            }

            //Initialization Phase
            if (app.Stage.equals(Constants.IN_STAGE)){

                Log.d(TAG, "type = "+ resp.getIntents().get(0).getName());
                String type = resp.getIntents().get(0).getName();
                Log.d(TAG, "type = "+resp.getIntents().get(0).getValue());
                app = Switcher.selectActionbyType(app,type);
                Log.d(TAG, "entered in init stage");
            }

            //Data Fill Phase
            if (app.Stage.equals(Constants.CH_STAGE)){
                Log.i(TAG,"entered in data fill stage ");


                //One time no multistage comminicators to pass data from appdata
                if( resp.getEntities().getAppData() != null && resp.getEntities().getAppData().get(0).getConfidence()> 0.8) {
                    Log.d("1",app.Current_Key+" "+resp.getEntities().getAppData().get(0).getValue());
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
                    Log.d("2",app.Current_Key+" "+resp.getEntities().getPhoneNumber().get(0).getValue());
                    app.data.put(app.Current_Key,resp.getEntities().getPhoneNumber().get(0).getValue());
                }

                if(resp.getEntities().getDatetime() !=null && resp.getEntities().getDatetime().get(0).getConfidence() >0.8 ) {
                    app.data.put(Constants.REM_KEY_TIME,resp.getEntities().getDatetime().get(0).getValue());
                    Log.i(TAG,"rem key time = "+resp.getEntities().getDatetime().get(0).getValue());
                }
                if(resp.getEntities().getDuration() !=null && resp.getEntities().getDuration().get(0).getConfidence() >0.8 ) {
                    app.data.put(Constants.TIMER_KEY,resp.getEntities().getDuration().get(0).getNormalized().getValue());
                    Log.i(TAG,"rem duration= "+resp.getEntities().getDuration().get(0).getNormalized().getValue());
                }


                //Multi stage comm gatherer
                if(resp.getText() != null && app.waiting_data && app.data.get(Constants.TIMER_KEY)==null && !(resp.getEntities().getDatetime() !=null
                        && resp.getEntities().getDatetime().get(0).getConfidence() >0.8 ) ){
                    app.data.put(app.Current_Key,resp.getText());
                    Log.i(TAG,"multistage data response from user is = "+resp.getText());
                }else if(app.waiting_data && (resp.getEntities().getDatetime() !=null &&  app.data.get(Constants.TIMER_KEY)==null
                        && resp.getEntities().getDatetime().get(0).getConfidence() >0.8 ) ){
                    Log.d("3",app.Current_Key+" "+resp.getEntities().getDatetime().get(0).getValue());
                    app.data.put(Constants.REM_KEY_TIME,resp.getEntities().getDatetime().get(0).getValue());
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
                    app.Stage = Constants.TR_STAGE;

                }



            }

            if (app.Stage.equals(Constants.TR_STAGE)){
                Log.i(TAG," entered in tr stage = "+app.data.get(app.Current_Key));
                app.waiting_data = false;
                app = Switcher.transforminfo(app,getApplicationContext());

            }


            if (app.Stage.equals(Constants.VR_STAGE)){

                speak(app.VERIFY_MESSAGE,true);
                app.Stage = Constants.AFTER_VR_STAGE;
                Log.i(TAG,"entered in vr stage= ");
            }

            if (app.Stage.equals(Constants.AFTER_VR_STAGE)){

                if(resp.getText().contains("ναι")) {
                    app.Stage = Constants.RUN_STAGE;
                }
                else if(resp.getText().contains("όχι") ) {
                     app.Stage = Constants.CP_STAGE;
                    EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
                    speak("όπως επιθυμείτε", false);
                }
                Log.i(TAG,"entered in after vr stage= ");
            }


            if (app.Stage.equals(Constants.RUN_STAGE)){
                app.runIntent(getApplicationContext());
                app.Stage = Constants.CP_STAGE;
                //EventBus.getDefault().postSticky(new Events.ActivatedRecognition(true));
                Log.i(TAG,"entered in run stage");
            }

            if (app.Stage.equals(Constants.NF_STAGE)){
                EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
                speak(app.NOT_FOUND,false);
                Constants.app.Init();
                Log.i(TAG,"entered in not found stage");
                app.Stage = Constants.CP_STAGE;

            }
            /**/ if (app.Stage.equals(Constants.CP_STAGE)){
            Log.i(TAG,"completed"+app.LAUNCHED);

            if(!app.LAUNCHED.equals(""))

                speak(app.LAUNCHED,false);
                Constants.app.Init();
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