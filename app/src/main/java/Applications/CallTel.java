package Applications;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.bill.speechclient.R;

import java.util.ArrayList;

import Utils.ContactUtils;

/**
 * Created by bill on 10/26/17.
 */

public class CallTel {


    public static String TriggerCall(final String data, Context context) {
        final ArrayList<String> tel = ContactUtils.ContactNumber(data, context);
        Log.i("contact size: ", String.valueOf(tel.size()));
        String msg = null;
        if (tel.size() == 1) {
            newCall(tel.get(0), context);
            msg = context.getResources().getString(R.string.make_call_acces_message);
        } else if (tel.size() <= 0) {
            msg = context.getResources().getString(R.string.make_call_error_message);
        } else if (tel.size() > 1) {
            newCallDialog(tel.toArray(new CharSequence[tel.size()]), context);
            msg = context.getResources().getString(R.string.make_call_acces_message);
        }
        return msg;
    }

    @SuppressLint("MissingPermission")
    private static void newCall(String number, Context context) {
        final Intent intent = new Intent(Constatns.actionCall);
        intent.setFlags(Constatns.flag);
        intent.setData(Uri.parse("tel:" + number));
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(intent);
    }

    private static void newCallDialog(final CharSequence[] number, final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Επελεξέ αριθμό");

        builder.setItems(number, new DialogInterface.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Intent intent = new Intent(Constatns.actionCall);
                intent.setFlags(Constatns.flag);
                intent.setData(Uri.parse("tel:" + number[i]));
                if (intent.resolveActivity(context.getPackageManager()) != null)
                    context.startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


}
