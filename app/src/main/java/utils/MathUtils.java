package utils;

import android.util.Log;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by bill on 11/6/17.
 */

public class MathUtils {

    static String TAG = "MathUtils";

    public static boolean isNumeric(String str) {

        Log.i(TAG, str);
        NumberFormat formatter = NumberFormat.getInstance();
        //ParsePosition is a simple class used by Format and its subclasses to keep track of the current position during parsing.
        ParsePosition pos = new ParsePosition(0);

        formatter.parse(str, pos);

        return str.length() == pos.getIndex();
    }

    public static HashMap<String,Integer> GetDtaeInfo(String Datetime){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",Locale.getDefault());
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date;
        HashMap datetime = new HashMap();
        try {

            date = sdf.parse(Datetime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int day = calendar.get(Calendar.DATE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            datetime.put("month",month);
            datetime.put("year",year);
            datetime.put("day",day);
            datetime.put("hour",hour);
            datetime.put("minute",minute);
        } catch (ParseException e) {
            datetime=null;

        }


        return datetime;
    }
}