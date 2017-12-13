package wit_connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import utils.SearchStringHelper;

/**
 * Created by bill on 11/4/17.
 */

public class WitResponse extends AsyncTask<String, Void, HashMap<String, String>> {

    public static final int STATUS_ERROR_COMMAND = 0;
    private static final String TAG = WitResponse.class.getSimpleName();
    private static final String witurl = "https://api.wit.ai/message?v=20171106&q=";
    private static final String accessToken = "Bearer NDU3VFL2EU27AYCMGLAUKF3X5TPFSYPN";
    private static final String header = "Authorization";
    private HashMap<String, String> WitResults = null;
    private WitResponseMessage msgListener;


    public WitResponse(WitResponseMessage msgListener) {
        this.msgListener = msgListener;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> StringHashMap) {

        final String conf = StringHashMap.get("Action_conf");
        final String application = StringHashMap.get("Action");
        final String search = StringHashMap.get("App_data");
        Log.i(TAG, "confidence of action is  " + conf + " action is " + application + "data is " + search);

        if (application == null) {
            msgListener.ErrorCommand(STATUS_ERROR_COMMAND);
        } else {
            msgListener.Message(search, application, conf);
        }

    }

    @Override
    protected HashMap<String, String> doInBackground(String... strings) {
        String query = strings[0];
        String queryencoded = null;
        Log.i(TAG, "query is " + query);

        try {
            //encode query for putting it to url
            queryencoded = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.i(TAG, "UnsupportedEncodingException: " + e.getMessage());
        }
        String witrequesturl = witurl + queryencoded;
        Log.i(TAG, "query url is " + witrequesturl);

        try {
            //put in hash map enities of json
            WitResults = SearchStringHelper.JsontoHash(GetResults(witrequesturl));
            Log.i(TAG, "Enities are " + WitResults.entrySet());

        } catch (IOException e) {
            Log.i(TAG, "IOException: " + e.getMessage());
        }

        return WitResults;
    }

    //get response from wit.ai
    private String GetResults(String url) throws IOException {

        HttpURLConnection connection = null;
        String result = null;
        InputStream stream = null;

        try {
            //create connection with wit
            URL urlWit = new URL(url);
            connection = (HttpURLConnection) urlWit.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty(header, accessToken);

            if (connection.getResponseCode() != 200) {
                Log.i(TAG, "HTTP error: " + connection.getResponseCode());
                throw new RuntimeException("failed: HTTP error code: " + connection.getResponseCode());
            }
            //put response in inputstream
            stream = connection.getInputStream();
            if (stream != null) {
                //make stram strin with function StreamToString
                result = SearchStringHelper.StreamToString(stream);
            }

        } finally {
            //free resources
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }


        return result;

    }



}