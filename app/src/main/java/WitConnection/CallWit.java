package WitConnection;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.bill.speechclient.R;

import java.text.NumberFormat;
import java.text.ParsePosition;

import Applications.CallTel;
import Applications.youtube;

import static android.content.ContentValues.TAG;

/**
 * Created by bill on 10/23/17.
 */

public class CallWit {

    private static final String accessToken ="Bearer 7L2SKE6KUZRVDNWSM7XWDNRS4UHUQM4S";
    private static final String header = "Authorization";
    private CallWitResponse response = null;



    private youtube YouT;
    private CallTel callTel;
    private ProgressBar WaitAction;

    public CallWit(Context activityContext, Activity activity)
    {
        WaitAction = (ProgressBar) activity.findViewById(R.id.progressBar2);
        YouT = new youtube(Intent.ACTION_SEARCH, activityContext);
        YouT.SetData("com.google.android.youtube");
        YouT.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        callTel = new CallTel(Intent.ACTION_CALL, activityContext);
        callTel.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

    }

    private static boolean isNumeric(String str) {

        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);

        return str.length() == pos.getIndex();
    }

    public void setResponse(CallWitResponse response) {
        this.response = response;
    }

    protected void onPreExecute() {

        WaitAction.setIndeterminate(true);
        WaitAction.setVisibility(View.VISIBLE);
    }


    protected void onPostExecute(Object o) {


        //Map<String,String> fetchjson = CreateJson(o.toString());
        String application = null;
        //  String application = fetchjson.get("application");
        if (application.equals("No value for app_to_open")) {
            response.ResponseMsg("Λάθος Εντολή");
            // cancel(true);
            WaitAction.setIndeterminate(false);
            WaitAction.setVisibility(View.INVISIBLE);
        }
        String search = null;
        //String search = fetchjson.get("app_search");
        Log.d("APPKind",application);


        switch (application)
        {
            case "Youtube":
                YouT.AddExtra("query", search);
                YouT.TriggerIntent();
                WaitAction.setIndeterminate(false);
                WaitAction.setVisibility(View.INVISIBLE);
                break;
            case "call":
                Log.i(TAG, "Name: " + search);

                if (isNumeric(search.replace(" ", ""))) {

                    callTel.setData(Uri.parse("tel:" + search));
                    WaitAction.setIndeterminate(false);
                    WaitAction.setVisibility(View.INVISIBLE);
                    callTel.TriggerIntent();
                } else {
                    //    String tel = SearchContact(search);
                    //  callTel.setData(Uri.parse("tel:" + tel));
                    WaitAction.setIndeterminate(false);
                    WaitAction.setVisibility(View.INVISIBLE);
                    callTel.TriggerIntent();
                }
                break;
            default:
                System.out.println("default");

        }

    }


}