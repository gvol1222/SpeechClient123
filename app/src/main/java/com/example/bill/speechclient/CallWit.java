package com.example.bill.speechclient;



import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by bill on 10/23/17.
 */

public class CallWit extends AsyncTask {

    private static final String accessToken ="Bearer 7L2SKE6KUZRVDNWSM7XWDNRS4UHUQM4S";
    private static final String header = "Authorization";
    private String value,text;
    private Context activityCOntext;



    public CallWit(Context activityCOntext)
    {
        this.activityCOntext = activityCOntext;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {

        super.onPostExecute(o);
        value = CreateJson(o.toString());
     Log.d("value!!!!!!!!",value);
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.setPackage("com.google.android.youtube");
        intent.putExtra("query", value);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityCOntext.startActivity(intent);

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
            //Log.d("BILLOG",messageencoded);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = urlString +messageencoded;
        String result = GetResults(url);
       // JSONObject json = CreateJson(result);
        //Log.i("info:", value+"text: "+text);


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

    private String CreateJson(String result){
        JSONObject jObject = null;
        try {
             jObject = new JSONObject(result);
            jObject = jObject.getJSONObject("entities");
            JSONArray app_to_open = jObject.getJSONArray("app_to_open");
            JSONArray contact = jObject.getJSONArray("app_search_text");
             value = contact.getJSONObject(0).getString("value");
          //  Log.i("info:", value+"text: "+text);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }




}
