package wit_connection;


import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import events.Events;
import utils.jsonparsers.Witobj;


public  class WitResponse {


    private static final String TAG = WitResponse.class.getSimpleName();
    private static final String accessToken = "Bearer CKZRXPXVE2D2XYFU34PYPQS6PLAFRR5R";
    private static final String header = "Authorization";




    public static JsonObjectRequest GetResults(String result)   {
        String query = result;
        String queryencoded = null;

        Log.i(TAG, "query is " + query);

        try {
            //encode query for putting it to url
            queryencoded = URLEncoder.encode(query, "UTF-8");
            Log.i(TAG, "query is " + queryencoded);
        } catch (UnsupportedEncodingException e) {
            Log.i(TAG, "UnsupportedEncodingException: " + e.getMessage());
        }
        String witurl = "https://api.wit.ai/message?v=20171106&q=";
        String fullwiturl = witurl + queryencoded;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, fullwiturl,null, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {


                Log.e("Your Array Response", String.valueOf(response));
                Witobj witResponse = new Gson().fromJson(response.toString(), Witobj.class);
                EventBus.getDefault().postSticky(new Events.WitREsp(witResponse,"WIT"));
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put(header, accessToken);
                return params;
            }

        };


        return request;
    }

}