package utils;

/**
 * Created by bill on 11/17/17.
 */

import android.util.Log;

import org.apache.commons.text.StrBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class SearchStringHelper {

    private static final String TAG = "SearchStringHelper";

    static HashMap<String, Double> getBestStringMatch(List<String> list, String query) {
        JaroWinkler jr = new JaroWinkler();
        String bestmatch = null;
        double max_match = 0.0;

        HashMap<String, Double> result = new HashMap<>();

        for (String elem : list) {

            //for greek names
            if (jr.similarity(greeknorm(query), greeknorm(elem)) > max_match) {
                max_match = jr.similarity(greeknorm(query), greeknorm(elem));
                bestmatch = elem;
                Log.i(TAG, "max match is: " + max_match);
                Log.i(TAG, "best match is: " + bestmatch);

            }
            //for english names
            if (jr.similarity(greeknorm(elem), greeklishnorm(query)) > max_match) {
                max_match = jr.similarity(greeknorm(elem), greeklishnorm(query));
                bestmatch = elem;
                Log.i(TAG, "max match is: " + max_match);
                Log.i(TAG, "best match is: " + bestmatch);
            }

            if (max_match > 0.75) {
                result.put(bestmatch, max_match);
            }
        }

        //if the  result is more than one
        //choose the string with better confidence
        if (result.size() > 1) {

            for (Map.Entry<String, Double> entry : result.entrySet()) {
                if (entry.getValue() > 0.9) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        } else {
            if (max_match < 0.75) {
                result.put("no_match", max_match);

            } else {
                result.put(bestmatch, max_match);

            }
        }

        return result;
    }

    public static double getContactSimilairty(String name, String query) {
        JaroWinkler jr = new JaroWinkler();
        double max_match = 0.0;
        String bestmatch = null;

        if (jr.similarity(greeknorm(query), greeknorm(name)) > max_match) {
            max_match = jr.similarity(greeknorm(query), greeknorm(name));
        }
        if (jr.similarity(greeknorm(name), greeklishnorm(query)) > max_match) {
            max_match = jr.similarity(greeknorm(name), greeklishnorm(query));
        }
        return max_match;
    }

    //functions for wit response class
    public static String StreamToString(InputStream stream) throws IOException {

        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[2000];
        int ReadSize;
        StringBuilder buffer = new StringBuilder();
        while ((ReadSize = reader.read(rawBuffer)) != -1) {

            if (ReadSize > 2000) {
                ReadSize = 2000;
            }
            buffer.append(rawBuffer, 0, ReadSize);
        }
        return buffer.toString();
    }


    public static HashMap<String, String> JsontoHash(String result) {
        HashMap<String, String> map = new HashMap<>();
        JSONObject reader;
        try {
            reader = new JSONObject(result);
            Log.i(TAG, "response from wit as json is " + reader.toString());

            JSONObject elements = reader.getJSONObject("entities");
            Log.i(TAG, " json elements is " + elements);

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

    private static String greeknorm(String word) {
        String norm = word.toLowerCase();
        StrBuilder wordbld = new StrBuilder(norm);

        wordbld.replaceAll("ά", "α");
        wordbld.replaceAll("έ", "ε");
        wordbld.replaceAll("ή", "η");
        wordbld.replaceAll("ί", "ι");
        wordbld.replaceAll("ό", "ο");
        wordbld.replaceAll("ύ", "υ");
        wordbld.replaceAll("ώ", "ω");
        wordbld.replaceAll("ς", "σ");

        return wordbld.toString();
    }

    private static String greeklishnorm(String word) {
        String norm = word.toLowerCase();
        StrBuilder wordbld = new StrBuilder(norm);


//        special cases
        wordbld.replaceAll("ου", "ou");
//        accent removal
        wordbld.replaceAll("ά", "α");
        wordbld.replaceAll("έ", "ε");
        wordbld.replaceAll("ή", "η");
        wordbld.replaceAll("ί", "ι");
        wordbld.replaceAll("ό", "ο");
        wordbld.replaceAll("ύ", "υ");
        wordbld.replaceAll("ώ", "ω");
        wordbld.replaceAll("ς", "σ");
//        translation
        wordbld.replaceAll("α", "a");
        wordbld.replaceAll("β", "b");
        wordbld.replaceAll("γ", "g");
        wordbld.replaceAll("δ", "d");
        wordbld.replaceAll("ε", "e");
        wordbld.replaceAll("ζ", "z");
        wordbld.replaceAll("η", "i");
        wordbld.replaceAll("θ", "th");
        wordbld.replaceAll("ι", "i");
        wordbld.replaceAll("κ", "k");
        wordbld.replaceAll("λ", "l");
        wordbld.replaceAll("μ", "m");
        wordbld.replaceAll("ν", "n");
        wordbld.replaceAll("ξ", "x");
        wordbld.replaceAll("ο", "o");
        wordbld.replaceAll("π", "p");
        wordbld.replaceAll("ρ", "r");
        wordbld.replaceAll("σ", "s");
        wordbld.replaceAll("τ", "t");
        wordbld.replaceAll("υ", "i");
        wordbld.replaceAll("φ", "f");
        wordbld.replaceAll("χ", "ch");
        wordbld.replaceAll("ψ", "ps");
        wordbld.replaceAll("ω", "o");


        return wordbld.toString();
    }

}
