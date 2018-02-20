package applications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;

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
    public HashMap<String,String> data ;
    //Request data from user with message
    public HashMap<String,String> data_requests;
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

    //Constructor
    public Action() {
        Init();
    }


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
        data = new HashMap<>();
        //Request data from user with message
        data_requests = new HashMap<>();
    }
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
        IntentURIprefix = Uri.EMPTY;
        UriQuery = "";
        RequiresUri = false;
        RequiresPackage = false;
        Package ="";
        RequiresExtra =false;
        extras = new Bundle();
    }
    //Run the Intent
    public void runIntent(Context con) {

        if(type.equals(Constatns.SEND_SMS)){
            Stage = Constatns.CP_STAGE;
            Sms.SendMessage(data.get(Current_Key), con, data.get(Constatns.SMS_APP_NAME));
        }
        else {
            Stage = Constatns.CP_STAGE;
            Intent curIntent = CreateIntent();
            con.startActivity(curIntent);
        }

    }

    //Helper Classes
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
            Log.d(TAG, "package: "+Package);
            runInt.putExtras(extras);
        }/**/
        return runInt;
    }




}