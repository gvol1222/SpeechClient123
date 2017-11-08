package Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

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
                YouT = new youtube(Intent.ACTION_SEARCH, context.getApplicationContext());
                YouT.SetData("com.google.android.youtube");
                YouT.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                YouT.AddExtra("query", search);
                YouT.TriggerIntent();
                break;
            case "make_call":
                final CallTel callTel;
                callTel = new CallTel(Intent.ACTION_CALL, context.getApplicationContext());
                callTel.AddFlag(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ArrayList<String> tel = ContactUtils.ContactNumber(context.getApplicationContext(), search);
                callTel.setData(Uri.parse("tel:" + tel.get(0)));
                callTel.TriggerIntent();
                break;
            default:
                break;
        }
    }


    private static void launchInent(final String packageName, Context context, String action) {
        final Intent AppIntent = new Intent(action);
        AppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        AppIntent.setPackage(packageName);

    }
}
