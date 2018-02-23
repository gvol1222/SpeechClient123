package utils;

import android.util.Log;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by bill on 11/6/17.
 */

public class MathUtils {

    static String TAG = "MathUtils";

    static boolean isNumeric(String str) {

        Log.i(TAG, str);
        NumberFormat formatter = NumberFormat.getInstance();
        //ParsePosition is a simple class used by Format and its subclasses to keep track of the current position during parsing.
        ParsePosition pos = new ParsePosition(0);

        formatter.parse(str, pos);

        return str.length() == pos.getIndex();
    }

    private static final String[] formats = {
            "yyyy-MM-dd'T'HH:mm:ss'Z'",   "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss",      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss",        "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
            "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS",
            "MM/dd/yyyy'T'HH:mm:ssZ",     "MM/dd/yyyy'T'HH:mm:ss",
            "yyyy:MM:dd HH:mm:ss",        "yyyyMMdd", };
    public static Date GetDtaeInfo(String Datetime){

        /* SimpleDateFormat sdf = null;
        if (Datetime != null) {
            for (String parse : formats) {
                 sdf = new SimpleDateFormat(parse);
                try {
                    sdf.parse(Datetime);
                    Log.i(TAG,"Printing the value of " + parse);
                } catch (ParseException e) {

                }
            }
        }*/

       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",Locale.getDefault());
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        try {

           date = sdf.parse(Datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}