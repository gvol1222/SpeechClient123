package applications;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;


import utils.ContactUtils;

/**
 * Created by gvol on 28/1/2018.
 */

public class Switcher {


    private static String TAG = "Switcher";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Action selectActionbyType(Action app, String type) {

        Log.i(TAG,"type of app is: "+app.type);
        LinkedHashMap<String,String> data_request = new LinkedHashMap<>();
        LinkedHashMap<String,String> data = new LinkedHashMap<>();
        if (type.equals(Constatns.CALL_APP)) {
            Log.i(TAG,"call app");

            data_request.put(Constatns.CALL_APP_NAME,Constatns.CALL_INFO_MESSAGE);
            data.put(Constatns.CALL_APP_NAME,null);
            app = InitActionObj(
                    app,type,Constatns.ACTION_CALL,true,Constatns.CALL_APP_NAME,
                    data_request,data,Constatns.CALL_URI,
                    false,Constatns.CALL_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE, Constatns.CALL_VER_MESSAGE,
                    false,"",false,""
            );
        }else if(type.equals(Constatns.SEND_SMS)){
            Log.i(TAG,"send sms app");

            data_request.put(Constatns.SMS_APP_NAME, Constatns.SMS_INFO_MESSAGE);
            data_request.put(Constatns.SMS_CONTENT_NAME,Constatns.SMS_CONTENT_MESSAGE);
            data.put(Constatns.SMS_APP_NAME,null);
            data.put(Constatns.SMS_CONTENT_NAME,null);
            app = InitActionObj(
                    app,type,"",false,Constatns.SMS_APP_NAME,
                    data_request,data,Constatns.SMS_URI,
                    false,
                    Constatns.SMS_NOT_FOUND_MESSAGE,Constatns.CH_STAGE,Constatns.SMS_VER_MESSAGE,
                    false,"",false,""
            );
        }else if(type.equals(Constatns.OPEN_APP)){
            Log.i(TAG,"open app");

            data_request.put(Constatns.OPEN_APP_NAME, Constatns.OPEN_INFO_MESSAGE);
            data.put(Constatns.OPEN_APP_NAME,null);

            app = InitActionObj(
                    app,type,Intent.CATEGORY_LAUNCHER,false,Constatns.OPEN_APP_NAME,
                    data_request,data,Constatns.OPEN_URI,
                    false,Constatns.OPEN_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.OPEN_VER_MESSAGE,
                    false,"",false,Constatns.OPEN_LAUNCHED_MESSAGE
            );
        }else if(type.equals(Constatns.DIRECTIONS)){
            Log.i(TAG,"directions");


            data_request.put(Constatns.MAPS_APP_NAME, Constatns.MAPS_INFO_MESSAGE);
            data.put(Constatns.MAPS_APP_NAME,null);
            app = InitActionObj(
                    app,type,Intent.ACTION_VIEW,true,Constatns.MAPS_APP_NAME,
                    data_request,data,Constatns.MAPS_URI,
                    false,Constatns.MAPS_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.MAPS_VER_MESSAGE,
                    true,Constatns.MAPS_PACKAGE,false,Constatns.MAPS_LAUNCHED_MESSAGE
            );
        }else if(type.equals(Constatns.PLAY_VIDEO)){
            Log.i(TAG,"play video");

            data_request.put(Constatns.VIDEO_APP_NAME, Constatns.VIDEO_INFO_MESSAGE);
            data.put(Constatns.VIDEO_APP_NAME,null);

            app = InitActionObj(
                    app,type,Intent.ACTION_SEARCH,false,Constatns.VIDEO_APP_NAME,
                    data_request,data,Constatns.VIDEO_URI,
                    false,Constatns.VIDEO_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.VIDEO_VER_MESSAGE,
                    true,Constatns.YOUTUBE_PACKAGE,true,Constatns.VIDEO_LAUNCHED_MESSAGE
            );
        }else if(type.equals(Constatns.PLAY_MUSIC)){
            Log.i(TAG,"play music");

            data_request.put(Constatns.MUSIC_APP_NAME, Constatns.MUSIC_INFO_MESSAGE);
            data.put(Constatns.MUSIC_APP_NAME,null);
            app = InitActionObj(
                    app,type,Constatns.MUSIC_SEARCH,false,Constatns.MUSIC_APP_NAME,
                    data_request,data,Constatns.MUSIC_URI,
                    false,Constatns.MUSIC_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.MUSIC_VER_MESSAGE,
                    false,"",true,Constatns.MUSIC_LAUNCHED_MESSAGE
            );

        }else if(type.equals(Constatns.SET_REMINDER)){
            Log.i(TAG,"set reminder");

            data_request.put(Constatns.REM_APP_NAME, Constatns.REM_CONTENT_MESSAGE);
            data_request.put(Constatns.REM_KEY_TIME, Constatns.REM_TIME_MESSAGE);
            data.put(Constatns.REM_APP_NAME,null);
            data.put(Constatns.REM_KEY_TIME,null);
            app = InitActionObj(
                    app,type,Constatns.MUSIC_SEARCH,false,Constatns.REM_APP_NAME,
                    data_request,data,Constatns.REM_URI,
                    true,Constatns.REM_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.REM_VER_MESSAGE,
                    false,"",true,Constatns.REM_SUCCESS_MESSAGE
            );
        }else if(type.equals(Constatns.SET_ALARM)){
            Log.i(TAG,"set alarm");

            data_request.put(Constatns.ALARM_DATE_TIME, Constatns.ALARM_TIME_MESSAGE);
            data.put(Constatns.ALARM_DATE_TIME,null);
            app = InitActionObj(
                    app,type,Constatns.ACTION_ALARM,false,Constatns.ALARM_DATE_TIME,
                    data_request,data,Constatns.ALARM_URI,
                    true,Constatns.ALARM_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,Constatns.ALARM_NOT_FOUND_MESSAGE,
                    false,"",true,Constatns.ALARM_SUCCESS_MESSAGE
            );
        }else if(type.equals(Constatns.SEARCH_GOOGLE)){
            Log.i(TAG,"search google");


            data_request.put(Constatns.GOOGLE_SEARCH_APP_NAME, Constatns.GOOGLE_SEARCH_INFO_MESSAGE);
            data.put(Constatns.GOOGLE_SEARCH_APP_NAME,null);
            app = InitActionObj(
                    app,type,Intent.ACTION_VIEW,true,Constatns.GOOGLE_SEARCH_APP_NAME,
                    data_request,data,Constatns.GOOGLE_SEARCH_URI,
                    false,Constatns.GOOGLE_SEARCH_NOT_FOUND_MESSAGE
                    ,Constatns.CH_STAGE,"",
                    false,Constatns.MAPS_PACKAGE,false,Constatns.GOOGLE_SEARCH_SUCCESS_MESSAGE
            );
        }else if(type.equals(Constatns.SET_TIMER)){
            Log.i(TAG,"set timer");

            data_request.put(Constatns.TIMER_KEY, Constatns.TIMER_INFO_MESSAGE);
            data.put(Constatns.TIMER_KEY,null);

            app = InitActionObj(
                    app,type,Constatns.ACTION_TIMER,false,Constatns.TIMER_KEY,
                    data_request,data,"",
                    false,""
                    ,Constatns.CH_STAGE,"",
                    false,"",true,Constatns.TIMER_SUCCESS_MESSAGE
            );
        }
        return app;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Action transforminfo (Action app, Context con){


        if (app.type.equals(Constatns.CALL_APP)){

            String query = app.data.get(Constatns.CALL_APP_NAME);

            if(app.entities.getPhoneNumber()!=null || app.entities.getNumber()!=null){

                Log.i(TAG,"CALL APP: found number "+query);
                app.data.put(Constatns.CALL_APP_NAME, query);
                app.UriQuery = app.data.get(Constatns.CALL_APP_NAME);
                app.Stage = Constatns.VR_STAGE;

            }else if (ContactUtils.ContactNumber(query,con).size() > 0 ) {
                Log.i(TAG,"CALL APP: found name ");
                app.data.put(Constatns.CALL_APP_NAME, ContactUtils.ContactNumber(app.data.get(Constatns.CALL_APP_NAME), con).get(0));
                app.UriQuery = app.data.get(Constatns.CALL_APP_NAME);
                app.Stage = Constatns.VR_STAGE;
            }
            else {
                Log.i(TAG,"CALL APP: not found tel ");
                app.Stage = Constatns.NF_STAGE;
            }

        }else if(app.type.equals(Constatns.SEND_SMS)){

            String query = app.data.get(Constatns.SMS_APP_NAME);
            app.UniqueAction=true;
            if(app.entities.getPhoneNumber()!=null || app.entities.getNumber()!=null){
                Log.i(TAG,"SEND SMS: found number ");
                app.Stage = Constatns.VR_STAGE;
                //app.UniqueAction=true;

            }else if (ContactUtils.ContactNumber(query,con).size() > 0 ) {
                Log.i(TAG,"SEND SMS: found name ");
                app.data.put(Constatns.SMS_APP_NAME, ContactUtils.ContactNumber(app.data.get(Constatns.SMS_APP_NAME), con).get(0));
                app.Stage = Constatns.VR_STAGE;
                //app.UniqueAction=true;
            }
            else {
                Log.i(TAG,"SEND SMS: not found tel ");
                app.Stage = Constatns.NF_STAGE;
            }

        }else if(app.type.equals(Constatns.OPEN_APP)){
            Log.i(TAG,"open app ");
            app.Stage = Constatns.RUN_STAGE;
            app.UniqueAction=true;

        }else if(app.type.equals(Constatns.DIRECTIONS)){
            Log.i(TAG,"open directions ");
            app.UriQuery = app.data.get(Constatns.MAPS_APP_NAME);
            Constatns.app.LAUNCHED = Constatns.app.LAUNCHED+" "+app.UriQuery;
            app.Stage = Constatns.RUN_STAGE;

        }else if(app.type.equals(Constatns.PLAY_VIDEO)){
            Log.i(TAG,"play video ");
            app.extras.putString(Constatns.VIDEO_EXTRA,app.data.get(Constatns.VIDEO_APP_NAME));
            app.Stage = Constatns.RUN_STAGE;
        }else if(app.type.equals(Constatns.PLAY_MUSIC)){
            Log.i(TAG,"play music ");
            app.extras.putString(MediaStore.EXTRA_MEDIA_FOCUS,Constatns.MUSIC_EXTRA);
            app.extras.putString(SearchManager.QUERY,app.data.get(Constatns.MUSIC_APP_NAME));
            app.Stage = Constatns.RUN_STAGE;
        }else if(app.type.equals(Constatns.SET_REMINDER)){
            Log.i(TAG,"reminder");

            if(app.data.get(Constatns.REM_APP_NAME)!=null && app.data.get(Constatns.REM_KEY_TIME)!=null ) {
                app.Stage = Constatns.RUN_STAGE;
                app.UniqueAction = true;
            }
            else {
                app.Stage = Constatns.CH_STAGE;
            }


        }else if(app.type.equals(Constatns.SET_ALARM)){
            Log.i(TAG,"set alarm");

            if( app.data.get(Constatns.ALARM_DATE_TIME)!=null ) {
                app.Stage = Constatns.RUN_STAGE;
                app.UniqueAction = true;
            }
            else {
                app.Stage = Constatns.CH_STAGE;
            }
        }else if(app.type.equals(Constatns.SEARCH_GOOGLE)){
            Log.i(TAG,"search google ");
            app.UriQuery = app.data.get(Constatns.GOOGLE_SEARCH_APP_NAME);

            app.Stage = Constatns.RUN_STAGE;

        }else if(app.type.equals(Constatns.SET_TIMER)){
            Log.i(TAG,"timer");
            app.extras.putInt(Constatns.TIMER_EXTRA, Integer.parseInt(app.data.get(Constatns.TIMER_KEY)));

            app.Stage = Constatns.RUN_STAGE;

        }
        return app;
    }



    private static Action InitActionObj(Action app, String type, String IntentAction, boolean requiresUri,
                                        String AppName, LinkedHashMap<String,String> data_request, LinkedHashMap<String, String> data, String uri,
                                        boolean MultiStageCommFromStart, String NOT_FOUND, String Stage,
                                        String VerifyMessage, Boolean requiresPackage, String Package, Boolean requiresExtra,String launched
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
        app.LAUNCHED = launched;

        return app;

    }


}