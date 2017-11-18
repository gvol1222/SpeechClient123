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
    private WitResponseMessage msgListener;


    public WitResponse(WitResponseMessage msgListener) {
        this.msgListener = msgListener;

    }


    @Override
    protected void onPostExecute(HashMap<String, String> StringHashMap) {
        String msg;

        final String conf = StringHashMap.get("Action_conf");
        final String application = StringHashMap.get("Action");
        final String search = StringHashMap.get("App_data");

        if (application == null) {
            msg = " Η εντολή δεν είναι σωστή προσπαθήστε ξανά";
            msgListener.ErrorCommand(msg);
            Log.i("errormesg:", msg);
        } else if (search == null) {
            msg = "Λάθος στην εντολή";
            msgListener.ErrorOnCommand(msg);
            Log.i("errorOnmesg:", msg);
        } else {
            msgListener.Message(search, application, conf);

        }

    }

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

    private HashMap<String, String> JsontoHash(String result) {
        Log.d("APPKind", result);
        HashMap<String, String> map = new HashMap<>();
        JSONObject reader;
        try {
            reader = new JSONObject(result);
            Log.i("reader:", reader.toString());
            JSONObject elements = reader.getJSONObject("entities");
            if (elements.has("Action")) {
                JSONArray action = elements.getJSONArray("Action");
                map.put("Action", action.getJSONObject(0).getString("value"));
                Double confid = action.getJSONObject(0).getDouble("confidence");
                map.put("Action_conf", confid.toString());
            } else {
                map.put("Action", null);
            }
            if (elements.has("App_data")) {
                JSONArray App_data = elements.getJSONArray("App_data");
                map.put("App_data", App_data.getJSONObject(0).getString("value"));
            } else {
                map.put("App_data", null);

            }



        } catch (JSONException e) {
            e.printStackTrace();

        }

        return map;
    }
}