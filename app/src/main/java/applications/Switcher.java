package applications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.util.Log;

import java.util.zip.CheckedOutputStream;

import utils.ContactUtils;

/**
 * Created by gvol on 28/1/2018.
 */

public class Switcher {


    private static String TAG = "Switcher";
    public static Action selectActionbyType(Action app, String type) {

        Log.i(TAG,"type of app is: "+app.type);

        if (type.equals(Constatns.CALL_APP)) {
            Log.i(TAG,"call app");
            app = InitActionObj(
                    app,type,Constatns.ACTION_CALL,true,Constatns.CALL_APP_NAME,
                    Constatns.CALL_INFO_MESSAGE,Constatns.CALL_URI,
                    false,Constatns.CALL_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE, Constatns.CALL_VER_MESSAGE
            );
        }else if(type.equals(Constatns.SEND_SMS)){
            Log.i(TAG,"send sms app");
            app = InitActionObj(
                    app,type,"SMS",false,Constatns.SMS_APP_NAME,
                    Constatns.SMS_INFO_MESSAGE,Constatns.SMS_URI,
                    false,
                    Constatns.SMS_NOT_FOUND_MESSAGE,Constatns.CH_STAGE,Constatns.SMS_VER_MESSAGE
            );
        }else if(type.equals(Constatns.OPEN_APP)){
            Log.i(TAG,"open app");
            app = InitActionObj(
                    app,type,Intent.CATEGORY_LAUNCHER,false,Constatns.OPEN_APP_NAME,
                    Constatns.OPEN_INFO_MESSAGE,Constatns.OPEN_URI,
                    false,Constatns.OPEN_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.OPEN_VER_MESSAGE
            );
        }
        return app;
    }

    public static Action transforminfo (Action app, Context con){


        if (app.type.equals(Constatns.CALL_APP)){

            String query = app.data.get("contact");

            if(app.entities.getPhoneNumber()!=null || app.entities.getNumber()!=null){

                Log.i(TAG,"found number ");
                app.data.put(Constatns.CALL_APP_NAME, query);
                app.UriQuery = app.data.get(Constatns.CALL_APP_NAME);
                app.Stage = Constatns.VR_STAGE;

            }else if (ContactUtils.ContactNumber(query,con).size() > 0 ) {
                Log.i(TAG,"found name ");
                app.data.put(Constatns.CALL_APP_NAME, ContactUtils.ContactNumber(app.data.get(Constatns.CALL_APP_NAME), con).get(0));
                app.UriQuery = app.data.get(Constatns.CALL_APP_NAME);
                app.Stage = Constatns.VR_STAGE;
            }
            else {
                Log.i(TAG,"not found tel ");
                app.Stage = Constatns.NF_STAGE;
            }
        }else if(app.type.equals(Constatns.SEND_SMS)){

            String query = app.data.get(Constatns.SMS_APP_NAME);

            if(app.entities.getPhoneNumber()!=null || app.entities.getNumber()!=null){
                Log.i(TAG,"found number ");
                app.data.put(Constatns.SMS_APP_NAME, query);
                app.Stage = Constatns.MULTI_COMMAND_FROM_START;
                app.Current_Key ="SMS";
                app.data_requests.put("SMS","Πείτε μου το κείμενο που επιθυμείτε να στείλετε");

            }else if (ContactUtils.ContactNumber(query,con).size() > 0 ) {
                app.data.put(Constatns.SMS_APP_NAME, ContactUtils.ContactNumber(app.data.get(Constatns.SMS_APP_NAME), con).get(0));
                app.Stage = Constatns.MULTI_COMMAND_FROM_START;
                app.Current_Key ="SMS";
                app.data_requests.put("SMS",Constatns.OPEN_CONTENT_MESSAGE);
            }
            else {
                Log.i(TAG,"not found tel ");
                app.Stage = Constatns.NF_STAGE;
            }
        }else if(app.type.equals(Constatns.OPEN_APP)){

            String query = app.data.get(app.Current_Key);
            app.Stage = LaunchApp.launchapplication(query, con);

        }else if(app.type.equals("h")){

            String query = app.data.get(app.Current_Key);
            app.Stage = LaunchApp.launchapplication(query, con);

        }
        return app;
    }



    private static Action InitActionObj(Action app,String type,String IntentAction,boolean requiresUri,
                                        String AppName,String SoundMessage,String uri,
                                        boolean MultiStageCommFromStart,String NOT_FOUND,String Stage,String VerifyMessage
    ){

        app.type = type;
        app.IntentAction = IntentAction;
        app.RequiresUri = requiresUri;
        app.data.put(AppName, null);
        app.data_requests.put(AppName, SoundMessage);
        app.Current_Key = AppName;
        app.IntentURIprefix = Uri.parse(uri);
        app.MultiStageCommFromStart = MultiStageCommFromStart;
        app.NOT_FOUND = NOT_FOUND;
        app.Stage = Stage;
        app.VERIFY_MESSAGE =VerifyMessage;

        return app;

    }


}