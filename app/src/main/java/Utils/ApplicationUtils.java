package Utils;

import android.content.Context;

import Applications.CallTel;
import Applications.MediaIntents;

/**
 * Created by bill on 11/7/17.
 */

public class ApplicationUtils {


    public static String Selection(String application, String search, Context context) {


        switch (application) {
            case "play_video":
                return MediaIntents.newYoutube(search, context);
            case "make_call":
                return CallTel.TriggerCall(search, context);
            default:
                return "Λάθος εντολή";
        }
    }

}
