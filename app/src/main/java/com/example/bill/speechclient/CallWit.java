package com.example.bill.speechclient;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by bill on 10/23/17.
 */

public class CallWit extends AsyncTask {

    private static final String accessToken ="Bearer 7L2SKE6KUZRVDNWSM7XWDNRS4UHUQM4S";
    private static final String header = "Authorization";
    private static Map<Character, Character> MAP_NORM;

    static { // Greek characters normalization
        MAP_NORM = new HashMap<Character, Character>();
        MAP_NORM.put('ά', 'α');
        MAP_NORM.put('έ', 'ε');
        MAP_NORM.put('ί', 'ι');
        MAP_NORM.put('ό', 'ο');
        MAP_NORM.put('ύ', 'υ');
        MAP_NORM.put('ή', 'η');
        MAP_NORM.put('ς', 'σ');
        MAP_NORM.put('ώ', 'ω');
        MAP_NORM.put('Ά', 'α');
        MAP_NORM.put('Έ', 'ε');
        MAP_NORM.put('Ί', 'ι');
        MAP_NORM.put('Ό', 'ο');
        MAP_NORM.put('Ύ', 'υ');
        MAP_NORM.put('Ή', 'η');
        MAP_NORM.put('Ώ', 'ω');
    }

    private Context activityCOntext;
    private youtube YouT;
    private CallTel callTel;
    private Activity activity;
    private ProgressBar WaitAction;

    public CallWit(Context activityCOntext, Activity activity)
    {
        this.activityCOntext = activityCOntext;
        this.activity = activity;
        WaitAction = (ProgressBar) activity.findViewById(R.id.progressBar2);
        YouT = new youtube(Intent.ACTION_SEARCH,activityCOntext);
        YouT.SetData("com.google.android.youtube");
        YouT.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        callTel = new CallTel(Intent.ACTION_CALL, activityCOntext);
        callTel.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

    }

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */
        }
    /* // If you have StringUtils, you can use it to calculate the edit distance:
    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
                               (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static String removeAccents(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(s);

        for (int i = 0; i < s.length(); i++) {
            Character c = MAP_NORM.get(sb.charAt(i));
            if (c != null) {
                sb.setCharAt(i, c.charValue());
            }
        }

        return sb.toString();
    }

    private static boolean isNumeric(String str) {

        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);

        return str.length() == pos.getIndex();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        WaitAction.setIndeterminate(true);
        WaitAction.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Object o) {

        super.onPostExecute(o);
        Map<String,String> fetchjson = CreateJson(o.toString());
        String application = fetchjson.get("application");
        String search = fetchjson.get("app_search");
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
                    String tel = SearchContact(search);
                    callTel.setData(Uri.parse("tel:" + tel));
                    WaitAction.setIndeterminate(false);
                    WaitAction.setVisibility(View.INVISIBLE);
                    callTel.TriggerIntent();
                }
                break;
            default:
                System.out.println("default");

        }

    }

    private String SearchContact(String query) {
        String number = null;
        HashMap<String, String> numbers = new HashMap<>();
        ContentResolver cr = activityCOntext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        name = name.toLowerCase();
                        name = removeAccents(name);
                        query = query.toLowerCase();
                        query = removeAccents(query);
                        Log.i(TAG, "N: " + name + " " + similarity(name, query));

                        if (similarity(name, query) > 0.600) {
                            //  Log.i(TAG, "Name: " + name);
                            // Log.i(TAG, "Phone Number: " + phoneNo);
                            Log.i(TAG, "N: " + name + " " + similarity(name, query));

                            numbers.put(name, phoneNo);
                        }

                    }


                    pCur.close();

                }
            }

            for (Map.Entry m : numbers.entrySet()) {

                if (numbers.size() > 1) {
                    Log.i("inf", m.getKey() + " " + m.getValue() + " " + numbers.size() + " " + similarity(m.getKey().toString(), query));

                    if (similarity(m.getKey().toString(), query) > 0.800) {
                        number = String.valueOf(m.getValue());
                        Log.i("inf", m.getKey() + " " + m.getValue() + " " + numbers.size());

                    }
                } else if (similarity(m.getKey().toString(), query) > 0.600) {
                    number = String.valueOf(m.getValue());
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return number;
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