package applications;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.util.HashMap;
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
            HashMap<String,String> data_request = new HashMap<>();
            HashMap<String,String> data = new HashMap<>();
            data_request.put(Constatns.CALL_APP_NAME,Constatns.CALL_INFO_MESSAGE);
            data.put(Constatns.CALL_APP_NAME,null);
            app = InitActionObj(
                    app,type,Constatns.ACTION_CALL,true,Constatns.CALL_APP_NAME,
                    data_request,data,Constatns.CALL_URI,
                    false,Constatns.CALL_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE, Constatns.CALL_VER_MESSAGE,
                    false,"",false
            );
        }else if(type.equals(Constatns.SEND_SMS)){
            Log.i(TAG,"send sms app");
            HashMap<String,String> data_request = new HashMap<>();
            HashMap<String,String> data = new HashMap<>();
            data_request.put(Constatns.SMS_APP_NAME, Constatns.SMS_INFO_MESSAGE);
            data_request.put("SMS",Constatns.OPEN_CONTENT_MESSAGE);
            data.put(Constatns.SMS_APP_NAME,null);
            data.put("SMS",null);
            app = InitActionObj(
                    app,type,"SMS",false,Constatns.SMS_APP_NAME,
                   data_request,data,Constatns.SMS_URI,
                    false,
                    Constatns.SMS_NOT_FOUND_MESSAGE,Constatns.CH_STAGE,Constatns.SMS_VER_MESSAGE,
                    false,"",false
            );
        }else if(type.equals(Constatns.OPEN_APP)){
            Log.i(TAG,"open app");
            HashMap<String,String> data_request = new HashMap<>();
            HashMap<String,String> data = new HashMap<>();
            data_request.put(Constatns.OPEN_APP_NAME, Constatns.OPEN_INFO_MESSAGE);
            data.put(Constatns.OPEN_APP_NAME,null);

            app = InitActionObj(
                    app,type,Intent.CATEGORY_LAUNCHER,false,Constatns.OPEN_APP_NAME,
                    data_request,data,Constatns.OPEN_URI,
                    false,Constatns.OPEN_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.OPEN_VER_MESSAGE,
                    false,"",false
            );
        }else if(type.equals(Constatns.DIRECTIONS)){
            Log.i(TAG,"directions");

            HashMap<String,String> data_request = new HashMap<>();
            HashMap<String,String> data = new HashMap<>();
            data_request.put(Constatns.MAPS_APP_NAME, Constatns.MAPS_INFO_MESSAGE);
            data.put(Constatns.MAPS_APP_NAME,null);
            app = InitActionObj(
                    app,type,Intent.ACTION_VIEW,true,Constatns.MAPS_APP_NAME,
                    data_request,data,Constatns.MAPS_URI,
                    false,Constatns.MAPS_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.MAPS_VER_MESSAGE,
                    true,Constatns.MAPS_PACKAGE,false
            );
        }else if(type.equals(Constatns.PLAY_VIDEO)){
            Log.i(TAG,"play video");

            HashMap<String,String> data_request = new HashMap<>();
            HashMap<String,String> data = new HashMap<>();
            data_request.put(Constatns.VIDEO_APP_NAME, Constatns.VIDEO_INFO_MESSAGE);
            data.put(Constatns.VIDEO_APP_NAME,null);

            app = InitActionObj(
                    app,type,Intent.ACTION_SEARCH,false,Constatns.VIDEO_APP_NAME,
                    data_request,data,Constatns.VIDEO_URI,
                    false,Constatns.VIDEO_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.VIDEO_VER_MESSAGE,
                    true,Constatns.YOUTUBE_PACKAGE,true
            );
        }else if(type.equals(Constatns.PLAY_MUSIC)){
            Log.i(TAG,"play music");

            HashMap<String,String> data_request = new HashMap<>();
            HashMap<String,String> data = new HashMap<>();
            data_request.put(Constatns.MUSIC_APP_NAME, Constatns.MUSIC_INFO_MESSAGE);
            data.put(Constatns.MUSIC_APP_NAME,null);
            app = InitActionObj(
                    app,type,Constatns.MUSIC_SEARCH,false,Constatns.MUSIC_APP_NAME,
                    data_request,data,Constatns.MUSIC_URI,
                    false,Constatns.MUSIC_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.MUSIC_VER_MESSAGE,
                    false,"",true
            );

        }else if(type.equals(Constatns.SET_REMINDER)){
            Log.i(TAG,"set reminder");

            HashMap<String,String> data_request = new HashMap<>();
            HashMap<String,String> data = new HashMap<>();
            data_request.put(Constatns.REM_APP_NAME, Constatns.REM_CONTENT_MESSAGE);
            data_request.put(Constatns.REM_KEY_TIME, Constatns.REM_TIME_MESSAGE);
            data.put(Constatns.REM_APP_NAME,null);
            data.put(Constatns.REM_KEY_TIME,null);
            app = InitActionObj(
                    app,type,Constatns.MUSIC_SEARCH,false,Constatns.REM_APP_NAME,
                    data_request,data,Constatns.REM_URI,
                    true,Constatns.REM_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.REM_VER_MESSAGE,
                    false,"",true
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
                app.Stage = Constatns.VR_STAGE;

                //app.data.put(Constatns.SMS_APP_NAME, query);
                //app.Stage = Constatns.MULTI_COMMAND_FROM_START;
               /* app.Current_Key ="SMS";
                app.data_requests.put("SMS",Constatns.OPEN_CONTENT_MESSAGE);
                */
            }else if (ContactUtils.ContactNumber(query,con).size() > 0 ) {
                app.data.put(Constatns.SMS_APP_NAME, ContactUtils.ContactNumber(app.data.get(Constatns.SMS_APP_NAME), con).get(0));
                app.Stage = Constatns.VR_STAGE;

                /*app.Stage = Constatns.MULTI_COMMAND_FROM_START;
                app.Current_Key ="SMS";
                app.data_requests.put("SMS",Constatns.OPEN_CONTENT_MESSAGE);*/
            }
            else {
                Log.i(TAG,"not found tel ");
                app.Stage = Constatns.NF_STAGE;
            }
        }else if(app.type.equals(Constatns.OPEN_APP)){

            String query = app.data.get(app.Current_Key);
            app.Stage = LaunchApp.launchapplication(query, con);

        }else if(app.type.equals(Constatns.DIRECTIONS)){
            app.UriQuery = app.data.get(Constatns.MAPS_APP_NAME);
            app.Stage = Constatns.RUN_STAGE;

        }else if(app.type.equals(Constatns.PLAY_VIDEO)){
            app.extras.putString(Constatns.VIDEO_EXTRA,app.data.get(Constatns.VIDEO_APP_NAME));
            app.Stage = Constatns.RUN_STAGE;
        }else if(app.type.equals(Constatns.PLAY_MUSIC)){
            app.extras.putString(MediaStore.EXTRA_MEDIA_FOCUS,Constatns.MUSIC_EXTRA);
            app.extras.putString(SearchManager.QUERY,app.data.get(Constatns.MUSIC_APP_NAME));
            app.Stage = Constatns.RUN_STAGE;
        }else if(app.type.equals(Constatns.SET_REMINDER)){
            Log.i(TAG,"reminder");


            if(app.data.get(Constatns.REM_APP_NAME)!=null && app.data.get(Constatns.REM_KEY_TIME)!=null )
                app.Stage = Constatns.RUN_STAGE;
            else {
                app.Stage = Constatns.CH_STAGE;
            }
            /*String datetime = app.entities.getDatetime().get(0).getValue();
            app.data.put(Constatns.REM_DATE_TIME, datetime);
            String query = app.entities.getAppData().get(0).getValue();;
            app.data.put(Constatns.REM_APP_NAME, query);
            app.Stage = Constatns.RUN_STAGE;
            //app.Stage = Constatns.MULTI_COMMAND_FROM_START;
            app.Current_Key =Constatns.REM_DATE_TIME;
            //app.data_requests.put(Constatns.REM_DATE_TIME,Constatns.REM_INFO_MESSAGE);
            */

        }
        return app;
    }



    private static Action InitActionObj(Action app, String type, String IntentAction, boolean requiresUri,
                                        String AppName, HashMap<String,String> data_request,HashMap<String,String> data, String uri,
                                        boolean MultiStageCommFromStart, String NOT_FOUND, String Stage,
                                        String VerifyMessage, Boolean requiresPackage, String Package, Boolean requiresExtra
    ){

        app.type = type;
        app.IntentAction = IntentAction;
        app.RequiresUri = requiresUri;
        app.data =data;
        app.data_requests = data_request;
        app.Current_Key = AppName;
        app.IntentURIprefix = Uri.parse(uri);
        app.MultiStageCommFromStart = MultiStageCommFromStart;
        app.NOT_FOUND = NOT_FOUND;
        app.Stage = Stage;
        app.VERIFY_MESSAGE =VerifyMessage;
        app.RequiresPackage = requiresPackage;
        app.Package =Package;
        app.RequiresExtra = requiresExtra;

        return app;

    }


}