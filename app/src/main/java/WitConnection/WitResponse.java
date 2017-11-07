package WitConnection;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by bill on 11/4/17.
 */

public class WitResponse extends AsyncTask<String, Void, HashMap<String, String>> {

    private static final String TAG = WitResponse.class.getSimpleName();
    private static final String witurl = "https://api.wit.ai/message?v=20171106&q=";
    private static final String accessToken = "Bearer NDU3VFL2EU27AYCMGLAUKF3X5TPFSYPN";
    private static final String header = "Authorization";
    private HashMap<String, String> WitResults = null;


    @Override
    protected HashMap<String, String> doInBackground(String... strings) {
        String query = strings[0];
        String queryencoded = null;
        try {
            queryencoded = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.i(TAG, "UnsupportedEncodingException: " + e.getMessage());
        }
        String witrequesturl = witurl + queryencoded;

        try {
            WitResults = JsontoHash(GetResults(witrequesturl));
        } catch (JSONException e) {
            Log.i(TAG, "JSONException: " + e.getMessage());

        } catch (IOException e) {
            Log.i(TAG, "IOException: " + e.getMessage());
        }

        return WitResults;
    }

    private String GetResults(String url) throws IOException {
        HttpURLConnection connection = null;
        String result = null;
        InputStream stream = null;

        try {
            URL urlWit = new URL(url);
            connection = (HttpURLConnection) urlWit.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty(header, accessToken);
            if (connection.getResponseCode() != 200) {
                Log.i(TAG, "HTTP error: " + connection.getResponseCode());
                throw new RuntimeException("failed: HTTP error code: " + connection.getResponseCode());
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = StreamToString(stream, 2000);
            }

        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }


        return result;

    }

    private String StreamToString(InputStream stream, int MaxReadSize) throws IOException, UnsupportedEncodingException {

        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[MaxReadSize];
        int ReadSize;
        StringBuffer buffer = new StringBuffer();
        while ((ReadSize = reader.read(rawBuffer)) != -1 && MaxReadSize > 0) {

            if (ReadSize > MaxReadSize) {
                ReadSize = MaxReadSize;
            }
            buffer.append(rawBuffer, 0, ReadSize);
            MaxReadSize -= ReadSize;
        }
        return buffer.toString();
    }

    private HashMap<String, String> JsontoHash(String result) throws JSONException {
        Log.d("APPKind", result);
        HashMap<String, String> map = new HashMap<>();
        JSONObject reader = new JSONObject(result);
        JSONObject elements = reader.getJSONObject("entities");
        JSONArray action = elements.getJSONArray("Action");
        JSONArray App_data = elements.getJSONArray("App_data");
        map.put("Action", action.getJSONObject(0).getString("value"));
        map.put("App_data", App_data.getJSONObject(0).getString("value"));
        return map;
    }
}
