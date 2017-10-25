package com.example.bill.speechclient;


import com.example.bill.speechclient.youtube;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.telecom.Call;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import java.util.Map;

/**
 * Created by bill on 10/23/17.
 */

public class CallWit extends AsyncTask {

    private static final String accessToken ="Bearer 7L2SKE6KUZRVDNWSM7XWDNRS4UHUQM4S";
    private static final String header = "Authorization";
    //private Context activityCOntext;
    private youtube YouT;


    public CallWit(Context activityCOntext)
    {
       // this.activityCOntext = activityCOntext;
        YouT = new youtube(Intent.ACTION_SEARCH,activityCOntext);
        YouT.SetData("com.google.android.youtube");
        YouT.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {

        super.onPostExecute(o);
        Map<String,String> fetchjson = CreateJson(o.toString());
        String application = fetchjson.get("application");
        String search = fetchjson.get("app_search");
        Log.d("APPKind",application);
        if (application.equals("Youtube"))
        {
          YouT.AddExtra("query",search);
          YouT.TriggerIntent();

        }

    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Object o) {
        super.onCancelled(o);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected String doInBackground(Object[] objects) {

        String urlString = objects[0].toString();
        String message  = objects[1].toString();
        String messageencoded  = null;

        try {
            messageencoded = URLEncoder.encode(message,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = urlString +messageencoded;
        String result = GetResults(url);
        return result;
    }

    private String GetResults(String url){
        HttpURLConnection connection;
        BufferedReader in =null;
        String line = null;
        String result = null;

        try {
            URL urlWit = new URL(url);
            connection = (HttpURLConnection) urlWit.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept","application/json");
            connection.setRequestProperty(header,accessToken);
            if (connection.getResponseCode() != 200){
                throw new RuntimeException("failed: HTTP error code: " +connection.getResponseCode());
            }
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));


        } catch (java.io.IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

        try {
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null ) {
                sb.append(line+"\n");
                Log.i("info:",sb.toString());


            }
            result = sb.toString();
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            try {
                if(in!=null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;

    }

    private Map<String, String> CreateJson(String result) {
        JSONObject jObject = null;
        Map<String, String> features = null;
        try {
            features = new HashMap<String, String>();
            jObject = new JSONObject(result);
            jObject = jObject.getJSONObject("entities");
            JSONArray app_to_open = jObject.getJSONArray("app_to_open");
            JSONArray contact = jObject.getJSONArray("app_search_text");
            String application = app_to_open.getJSONObject(0).getString("value");
            String value = contact.getJSONObject(0).getString("value");
            features.put("application", application);
            features.put("app_search", value);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return features;
    }




}