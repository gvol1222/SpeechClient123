package applications;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import recogniton_service.Interact;

/**
 * Created by gvol on 11/26/17.
 */

public class AppIntentService extends IntentService {

    public static final String APP_KIND = "app_kind";
    public static final String QUERY = "qry";
    public static final String CONFIDENCE = "confidence";
    public static final String RESULT = "msg";
    public static String tel;

    public AppIntentService() {
        super("AppIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = getApplicationContext();
        String application = null;
        if (intent != null) {
            application = intent.getStringExtra(APP_KIND);
        }
        String search = null;
        if (intent != null) {
            search = intent.getStringExtra(QUERY);
        }
        if (intent != null) {
            double confidence = intent.getDoubleExtra(CONFIDENCE, 0.0);
        }
        Log.d("ATTENTION", application + " : " + search);
        String[] result = new String[2];

        if (application != null) {
            switch (application) {
                case "open_app":
                    result[0] = LaunchApp.launchapplication(search, context);
                    break;
                case "play_video":
                    result[0] = MediaIntents.newYoutube(search, context);
                    break;
                case "make_call":
                    result = CallTel.TriggerCall(search, context);
                    break;
                case "directions":
                    result[0] = MapsIntent.GoogleMaps(search, context);
                    break;
                case "play_music":
                    result[0] = MediaIntents.MusicPlayer(search, context);
                    break;
                case "send sms":
                    result = CallTel.TriggerCall(search, context);
                    result[3] = "sms";
                    break;
                default:
                    result[0] = "Δεν βρέθηκε η εντολή. Πείτε μου ξανά.";
                    break;
            }
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Interact.ResponseReceiver.LOCAL_ACTION);
        broadcastIntent.putExtra(RESULT, result);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(broadcastIntent);

        }
    }

