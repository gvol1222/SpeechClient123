package Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import Applications.CallTel;
import Applications.youtube;

import static android.content.ContentValues.TAG;

/**
 * Created by bill on 11/7/17.
 */

public class ApplicationUtils {


    public static void Selection(String application, String search, Context context) {
        final youtube YouT;
        final CallTel callTel;
        YouT = new youtube(Intent.ACTION_SEARCH, context.getApplicationContext());
        YouT.SetData("com.google.android.youtube");
        YouT.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        callTel = new CallTel(Intent.ACTION_CALL, context.getApplicationContext());
        callTel.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        switch (application) {
            case "play_video":
                YouT.AddExtra("query", search);
                YouT.TriggerIntent();

                break;
            case "make_call":
                Log.i(TAG, "Name: " + search);
                ArrayList<String> tel = ContactUtils.ContactNumber(context.getApplicationContext(), search);
                callTel.setData(Uri.parse("tel:" + tel.get(0)));
                callTel.TriggerIntent();
                break;

            default:
                break;
        }
    }
}
