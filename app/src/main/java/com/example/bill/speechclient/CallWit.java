package com.example.bill.speechclient;



import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;


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
    private String message;

    public CallWit() {
        super();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
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
    protected Object doInBackground(Object[] objects) {

        String urlString = objects[0].toString();
        String message  = objects[1].toString();

        String messageencoded  = null;
        try {
            messageencoded = URLEncoder.encode(message,"UTF-8");
            Log.d("BILLOG",messageencoded);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = "";
        BufferedReader in =null;
        HttpURLConnection connection;
        try {
            URL url = new URL(urlString+messageencoded);
            connection = (HttpURLConnection) url.openConnection();
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
            while ((result = in.readLine()) != null )
                Log.i("info:",result);
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
