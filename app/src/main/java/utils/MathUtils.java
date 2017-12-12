package utils;

import android.util.Log;

import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Created by bill on 11/6/17.
 */

class MathUtils {

    static String TAG = "MathUtils";

    static boolean isNumeric(String str) {

        Log.i(TAG, str);
        NumberFormat formatter = NumberFormat.getInstance();
        //ParsePosition is a simple class used by Format and its subclasses to keep track of the current position during parsing.
        ParsePosition pos = new ParsePosition(0);

        formatter.parse(str, pos);

        return str.length() == pos.getIndex();
    }
}
