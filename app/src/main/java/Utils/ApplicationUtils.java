package Utils;

import android.content.Context;
import android.content.Intent;

import Applications.CallTel;
import Applications.youtube;

/**
 * Created by bill on 11/7/17.
 */

public class ApplicationUtils {


    public static void Selection(String application, String search, Context context) {


        switch (application) {
            case "play_video":
                final youtube YouT;
                YouT = new youtube(Intent.ACTION_SEARCH, context);
                YouT.SetData("com.google.android.youtube");
                YouT.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                YouT.AddExtra("query", search);
                YouT.TriggerIntent();
                break;
            case "make_call":
                CallTel.TriggerCall(search, context);
                break;
            default:
                break;
        }
    }

}
