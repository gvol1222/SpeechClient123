package utils;

import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Created by bill on 11/6/17.
 */

public class MathUtils {


    public static boolean isNumeric(String str) {

        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);

        return str.length() == pos.getIndex();
    }
}
