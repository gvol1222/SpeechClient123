package applications;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import applications.unique_apps.Sms;
import applications.unique_apps.UniqueSwitcher;
import utils.jsonparsers.Entities;

/**
 * Created by gvol on 17/12/2017.
 */

public class Action implements Serializable {
    //Config Variables
    private String TAG = "ActionClass";

    /*Stage parms*/
    public String type;
    public boolean RequiresVerification;
    public boolean MultiStageCommFromStart;
    public boolean waiting_data;
    public String Current_Key;
    public String Stage;

    /*App Data */
    //Data for apps demanding content like sms
    public LinkedHashMap<String,String> data ;
    //Request data from user with message
    public LinkedHashMap<String,String> data_requests;
    /*Intents Parms*/

    public String IntentAction;
    public boolean RequiresUri;
    public boolean RequiresPackage;
    public boolean RequiresExtra;
    public Bundle extras;
    public Uri IntentURIprefix = Uri.EMPTY;
    public String UriQuery;
    public String Package ;

    public Entities entities;

    //Messages
    public String VERIFY_MESSAGE ;
    public String MULTI_STAGE_MESSAGE ;
    public String ACTION_FAILED ;
    public String NOT_FOUND ;
    public String LAUNCHED ;
    public boolean UniqueAction;

    //Constructor
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Action() {
        Init();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void Init(){
        setIntentParms();
        setStageParms();
        setData();
        setMessages();


    }
    private void setMessages(){
        //Messages
        VERIFY_MESSAGE = "";
        ACTION_FAILED = "";
        NOT_FOUND = "";
        LAUNCHED = "";
        MULTI_STAGE_MESSAGE="";
    }
    private void setData(){
        //Data for apps demanding content like sms
        data = new LinkedHashMap<>();
        //Request data from user with message
        data_requests = new LinkedHashMap<>();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setStageParms(){
        type = "";
        RequiresVerification = false;
        MultiStageCommFromStart = false;
        waiting_data = false;
        Current_Key = "";
        Stage = Constatns.IN_STAGE;
    }
    private void setIntentParms(){
        IntentAction = null;
        UniqueAction = false;
        IntentURIprefix = Uri.EMPTY;
        UriQuery = "";
        RequiresUri = false;
        RequiresPackage = false;
        Package ="";
        RequiresExtra =false;
        extras = new Bundle();
    }
    //Run the Intent
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void runIntent(Context con) {

        if(UniqueAction ){
            //Stage = Constatns.CP_STAGE;
            UniqueSwitcher.Switcher(type,data,con);
        }
        else {
            Stage = Constatns.CP_STAGE;
            Intent curIntent = CreateIntent();
            Log.d(TAG, "package: "+curIntent);
            con.startActivity(curIntent);
        }

    }

    //Helper Classes
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Intent CreateIntent() {
        Intent runInt;
        if (RequiresUri) {
            Log.d(TAG, "uri query: "+UriQuery);
            runInt = new Intent(IntentAction, Uri.parse(IntentURIprefix + Uri.encode(UriQuery, "UTF-8")));
        } else {
            runInt = new Intent(IntentAction);
        }
        runInt.setFlags(Constatns.FLAGS);
        if(RequiresPackage){
            Log.d(TAG, "package: "+Package);
            runInt.setPackage(Package);
        }/**/
        if(RequiresExtra){
            Log.d(TAG, "package: "+extras);
            runInt.putExtras(extras);
        }/**/
        return runInt;
    }


}