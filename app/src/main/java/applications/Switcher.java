package applications;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import android.util.Log;

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
        if (type.equals(Constants.CALL_APP)) {
            Log.i(TAG,"call app");

            data_request.put(Constants.CALL_APP_NAME, Constants.CALL_INFO_MESSAGE);
            data.put(Constants.CALL_APP_NAME,null);
            app = InitActionObj(
                    app,type, Constants.ACTION_CALL,true, Constants.CALL_APP_NAME,
                    data_request,data, Constants.CALL_URI,
                    false, Constants.CALL_NOT_FOUND_MESSAGE
                    , Constants.CH_STAGE, Constants.CALL_VER_MESSAGE,
                    false,"",false,""
            );
        }else if(type.equals(Constants.SEND_SMS)){
            Log.i(TAG,"send sms app");

            data_request.put(Constants.SMS_APP_NAME, Constants.SMS_INFO_MESSAGE);
            data_request.put(Constants.SMS_CONTENT_NAME, Constants.SMS_CONTENT_MESSAGE);
            data.put(Constants.SMS_APP_NAME,null);
            data.put(Constants.SMS_CONTENT_NAME,null);
            app = InitActionObj(
                    app,type,"",false, Constants.SMS_APP_NAME,
                    data_request,data, Constants.SMS_URI,
                    false,
                    Constants.SMS_NOT_FOUND_MESSAGE, Constants.CH_STAGE, Constants.SMS_VER_MESSAGE,
                    false,"",false,""
            );
        }else if(type.equals(Constants.OPEN_APP)){
            Log.i(TAG,"open app");

            data_request.put(Constants.OPEN_APP_NAME, Constants.OPEN_INFO_MESSAGE);
            data.put(Constants.OPEN_APP_NAME,null);

            app = InitActionObj(
                    app,type,Intent.CATEGORY_LAUNCHER,false, Constants.OPEN_APP_NAME,
                    data_request,data, Constants.OPEN_URI,
                    false, Constants.OPEN_NOT_FOUND_MESSAGE
                    , Constants.CH_STAGE, Constants.OPEN_VER_MESSAGE,
                    false,"",false, Constants.OPEN_LAUNCHED_MESSAGE
            );
        }else if(type.equals(Constants.DIRECTIONS)){
            Log.i(TAG,"directions");


            data_request.put(Constants.MAPS_APP_NAME, Constants.MAPS_INFO_MESSAGE);
            data.put(Constants.MAPS_APP_NAME,null);
            app = InitActionObj(
                    app,type,Intent.ACTION_VIEW,true, Constants.MAPS_APP_NAME,
                    data_request,data, Constants.MAPS_URI,
                    false, Constants.MAPS_NOT_FOUND_MESSAGE
                    , Constants.CH_STAGE, Constants.MAPS_VER_MESSAGE,
                    true, Constants.MAPS_PACKAGE,false, Constants.MAPS_LAUNCHED_MESSAGE
            );
        }else if(type.equals(Constants.PLAY_VIDEO)){
            Log.i(TAG,"play video");

            data_request.put(Constants.VIDEO_APP_NAME, Constants.VIDEO_INFO_MESSAGE);
            data.put(Constants.VIDEO_APP_NAME,null);

            app = InitActionObj(
                    app,type,Intent.ACTION_SEARCH,false, Constants.VIDEO_APP_NAME,
                    data_request,data, Constants.VIDEO_URI,
                    false, Constants.VIDEO_NOT_FOUND_MESSAGE
                    , Constants.CH_STAGE, Constants.VIDEO_VER_MESSAGE,
                    true, Constants.YOUTUBE_PACKAGE,true, Constants.VIDEO_LAUNCHED_MESSAGE
            );
        }else if(type.equals(Constants.PLAY_MUSIC)){
            Log.i(TAG,"play music");

            data_request.put(Constants.MUSIC_APP_NAME, Constants.MUSIC_INFO_MESSAGE);
            data.put(Constants.MUSIC_APP_NAME,null);
            app = InitActionObj(
                    app,type, Constants.MUSIC_SEARCH,false, Constants.MUSIC_APP_NAME,
                    data_request,data, Constants.MUSIC_URI,
                    false, Constants.MUSIC_NOT_FOUND_MESSAGE
                    , Constants.CH_STAGE, Constants.MUSIC_VER_MESSAGE,
                    false,"",true, Constants.MUSIC_LAUNCHED_MESSAGE
            );

        }else if(type.equals(Constants.SET_REMINDER)){
            Log.i(TAG,"set reminder");

            data_request.put(Constants.REM_APP_NAME, Constants.REM_CONTENT_MESSAGE);
            data_request.put(Constants.REM_KEY_TIME, Constants.REM_TIME_MESSAGE);
            data.put(Constants.REM_APP_NAME,null);
            data.put(Constants.REM_KEY_TIME,null);
            app = InitActionObj(
                    app,type, Constants.MUSIC_SEARCH,false, Constants.REM_APP_NAME,
                    data_request,data, Constants.REM_URI,
                    true, Constants.REM_NOT_FOUND_MESSAGE
                    , Constants.CH_STAGE, Constants.REM_VER_MESSAGE,
                    false,"",true, Constants.REM_SUCCESS_MESSAGE
            );
        }else if(type.equals(Constants.SET_ALARM)){
            Log.i(TAG,"set alarm");

            data_request.put(Constants.ALARM_DATE_TIME, Constants.ALARM_TIME_MESSAGE);
            data.put(Constants.ALARM_DATE_TIME,null);
            app = InitActionObj(
                    app,type, Constants.ACTION_ALARM,false, Constants.ALARM_DATE_TIME,
                    data_request,data, Constants.ALARM_URI,
                    true, Constants.ALARM_NOT_FOUND_MESSAGE
                    , Constants.CH_STAGE, Constants.ALARM_NOT_FOUND_MESSAGE,
                    false,"",true, Constants.ALARM_SUCCESS_MESSAGE
            );
        }else if(type.equals(Constants.SEARCH_GOOGLE)){
            Log.i(TAG,"search google");


            data_request.put(Constants.GOOGLE_SEARCH_APP_NAME, Constants.GOOGLE_SEARCH_INFO_MESSAGE);
            data.put(Constants.GOOGLE_SEARCH_APP_NAME,null);
            app = InitActionObj(
                    app,type,Intent.ACTION_VIEW,true, Constants.GOOGLE_SEARCH_APP_NAME,
                    data_request,data, Constants.GOOGLE_SEARCH_URI,
                    false, Constants.GOOGLE_SEARCH_NOT_FOUND_MESSAGE
                    , Constants.CH_STAGE,"",
                    false, Constants.MAPS_PACKAGE,false, Constants.GOOGLE_SEARCH_SUCCESS_MESSAGE
            );
        }else if(type.equals(Constants.SET_TIMER)){
            Log.i(TAG,"set timer");

            data_request.put(Constants.TIMER_KEY, Constants.TIMER_INFO_MESSAGE);
            data.put(Constants.TIMER_KEY,null);

            app = InitActionObj(
                    app,type, Constants.ACTION_TIMER,false, Constants.TIMER_KEY,
                    data_request,data,"",
                    false, Constants.TIMER_NOT_FOUND_MESSAGE
                    , Constants.CH_STAGE,"",
                    false,"",true, Constants.TIMER_SUCCESS_MESSAGE
            );
        }
        return app;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Action transforminfo (Action app, Context con){


        if (app.type.equals(Constants.CALL_APP)){

            String query = app.data.get(Constants.CALL_APP_NAME);



            if(app.entities.getPhoneNumber()!=null || app.entities.getNumber()!=null){

                Log.i(TAG,"CALL APP: found number "+query);
                app.data.put(Constants.CALL_APP_NAME, query);
                app.UriQuery = app.data.get(Constants.CALL_APP_NAME);
                app.Stage = Constants.VR_STAGE;

            }else if (ContactUtils.ContactNumber(query,con).size() > 0 ) {
                Log.i(TAG,"CALL APP: found name ");
                app.data.put(Constants.CALL_APP_NAME, ContactUtils.ContactNumber(app.data.get(Constants.CALL_APP_NAME), con).get(0));
                app.UriQuery = app.data.get(Constants.CALL_APP_NAME);
                app.Stage = Constants.VR_STAGE;
            }
            else {
                Log.i(TAG,"CALL APP: not found tel ");
                app.Stage = Constants.NF_STAGE;
            }

        }else if(app.type.equals(Constants.SEND_SMS)){

            String query = app.data.get(Constants.SMS_APP_NAME);
            app.UniqueAction=true;
            if(app.entities.getPhoneNumber()!=null || app.entities.getNumber()!=null){
                Log.i(TAG,"SEND SMS: found number ");
                app.Stage = Constants.VR_STAGE;
                //app.UniqueAction=true;

            }else if (ContactUtils.ContactNumber(query,con).size() > 0 ) {
                Log.i(TAG,"SEND SMS: found name ");
                app.data.put(Constants.SMS_APP_NAME, ContactUtils.ContactNumber(app.data.get(Constants.SMS_APP_NAME), con).get(0));
                app.Stage = Constants.VR_STAGE;
                //app.UniqueAction=true;
            }
            else {
                Log.i(TAG,"SEND SMS: not found tel ");
                app.Stage = Constants.NF_STAGE;
            }

        }else if(app.type.equals(Constants.OPEN_APP)){
            Log.i(TAG,"open app ");
            app.Stage = Constants.RUN_STAGE;
            app.UniqueAction=true;

        }else if(app.type.equals(Constants.DIRECTIONS)){
            Log.i(TAG,"open directions ");
            app.UriQuery = app.data.get(Constants.MAPS_APP_NAME);
            Constants.app.LAUNCHED = Constants.app.LAUNCHED+" "+app.UriQuery;
            app.Stage = Constants.RUN_STAGE;

        }else if(app.type.equals(Constants.PLAY_VIDEO)){
            Log.i(TAG,"play video ");
            app.extras.putString(Constants.VIDEO_EXTRA,app.data.get(Constants.VIDEO_APP_NAME));
            app.Stage = Constants.RUN_STAGE;
        }else if(app.type.equals(Constants.PLAY_MUSIC)){
            Log.i(TAG,"play music ");
            app.extras.putString(MediaStore.EXTRA_MEDIA_FOCUS, Constants.MUSIC_EXTRA);
            app.extras.putString(SearchManager.QUERY,app.data.get(Constants.MUSIC_APP_NAME));
            app.Stage = Constants.RUN_STAGE;
        }else if(app.type.equals(Constants.SET_REMINDER)){
            Log.i(TAG,"reminder");

            if(app.data.get(Constants.REM_APP_NAME)!=null && app.data.get(Constants.REM_KEY_TIME)!=null ) {
                app.Stage = Constants.RUN_STAGE;
                app.UniqueAction = true;
            }
            else {
                app.Stage = Constants.CH_STAGE;
            }


        }else if(app.type.equals(Constants.SET_ALARM)){
            Log.i(TAG,"set alarm");

            if( app.data.get(Constants.ALARM_DATE_TIME)!=null ) {
                app.Stage = Constants.RUN_STAGE;
                app.UniqueAction = true;
            }
            else {
                app.Stage = Constants.CH_STAGE;
            }
        }else if(app.type.equals(Constants.SEARCH_GOOGLE)){
            Log.i(TAG,"search google ");
            app.UriQuery = app.data.get(Constants.GOOGLE_SEARCH_APP_NAME);

            app.Stage = Constants.RUN_STAGE;

        }else if(app.type.equals(Constants.SET_TIMER)){
            Log.i(TAG,"timer");


            if( app.entities.getDuration()!=null){
                app.extras.putInt(Constants.TIMER_EXTRA, Integer.parseInt(app.data.get(Constants.TIMER_KEY)));
                app.Stage = Constants.RUN_STAGE;
            }else{
                app.Stage = Constants.NF_STAGE;
            }


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