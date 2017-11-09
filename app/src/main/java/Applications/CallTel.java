package Applications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import Utils.ContactUtils;

/**
 * Created by bill on 10/26/17.
 */

public class CallTel {


    public static void TriggerCall(@NonNull String data, @NonNull Context context) {
        final ArrayList<String> tel = ContactUtils.ContactNumber(data, context);
        if (tel.size() == 1) {
            newCall(Constatns.action, Constatns.flag, tel.get(0), context);
        }

    }

    private static void newCall(@NonNull  final String action, @NonNull final int flag, @NonNull String number, @NonNull Context context) {
        final Intent intent = new Intent(action);
        intent.setFlags(flag);
        intent.setData(Uri.parse("tel:" + number));
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(intent);
    }


}
