package Applications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.bill.speechclient.R;

import java.util.ArrayList;

import Utils.ContactUtils;

/**
 * Created by bill on 10/26/17.
 */

public class CallTel {


    public static String TriggerCall(final String data, Context context) {
        final ArrayList<String> tel = ContactUtils.ContactNumber(data, context);

        if (tel.size() == 1) {
            newCall(Constatns.action, Constatns.flag, tel.get(0), context);
            return context.getResources().getString(R.string.make_call_acces_message);
        } else if (tel.size() < 0 || tel == null) {
            return context.getResources().getString(R.string.make_call_error_message);
        } else {

        }
        return "";
    }

    private static void newCall(final String action, final int flag, String number, Context context) {
        final Intent intent = new Intent(action);
        intent.setFlags(flag);
        intent.setData(Uri.parse("tel:" + number));
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(intent);
    }


}
