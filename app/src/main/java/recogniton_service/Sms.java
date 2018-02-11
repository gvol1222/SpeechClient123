package recogniton_service;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.bill.Activities.R;

import java.util.ArrayList;

/**
 * Created by bill on 12/16/17.
 */

public class Sms {


    public static String SendMessage(String result, Context context, String tel) {

        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";
        String response = "";

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(SMS_DELIVERED), 0);


// For when the SMS has been sent
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, context.getResources().getString(R.string.sms_successfully_sented), Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, context.getResources().getString(R.string.sms_not_sented), Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, context.getResources().getString(R.string.sms_service_not_available), Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context,context.getResources().getString(R.string.sms_radio_off), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

// For when the SMS has been delivered
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, context.getResources().getString(R.string.sms_delivered), Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, context.getResources().getString(R.string.sms_not_delivered), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));

// Send a text based SMS
        SmsManager smsManager = SmsManager.getDefault();
// Send a text based SMS
        smsManager.sendTextMessage(tel, null, result, sentPendingIntent, deliveredPendingIntent);
    return response;
    }
}
