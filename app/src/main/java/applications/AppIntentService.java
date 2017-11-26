package applications;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import activities.MainActivity;

/**
 * Created by gvol on 11/26/17.
 */

public class AppIntentService extends IntentService {

    public static final String APP_KIND = "app_kind";
    public static final String QUERY = "qry";
    public static final String CONFIDENCE = "confidence";
    public static final String RESULT = "msg";


    public AppIntentService() {
        super("AppIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = getApplicationContext();
        String application = intent.getStringExtra(APP_KIND);
        String search = intent.getStringExtra(QUERY);
        double confidence = intent.getDoubleExtra(CONFIDENCE, 0.0);
        Log.d("ATTENTION", application + " : " + search);
        String result;

        switch (application) {
            case "open_app":
                result = LaunchApp.launchapplication(search, context);
                break;
            case "play_video":
                result = MediaIntents.newYoutube(search, context);
                break;
            case "make_call":
                result = CallTel.TriggerCall(search, context);
                break;
            case "directions":
                result = MapsIntent.GoogleMaps(search, context);
                break;
            case "play_music":
                result = MediaIntents.MusicPlayer(search, context);
                break;
            default:
                result = "Δεν βρέθηκε η εντολή. Πείτε μου ξανά.";
                break;
        }
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.ResponseReceiver.LOCAL_ACTION);
                broadcastIntent.putExtra(RESULT, result);
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(broadcastIntent);

        }
    }

