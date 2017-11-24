package utils;

import android.content.Context;

import applications.CallTel;
import applications.LaunchApp;
import applications.MapsIntent;
import applications.MediaIntents;

/**
 * Created by bill on 11/7/17.
 */

public class ApplicationUtils {


    public static String Selection(String application, String search, String confidence,Context context) {

            switch (application) {
                case "open_app":
                    return LaunchApp.launchapplication(search, context);
                case "play_video":
                    return MediaIntents.newYoutube(search, context);
                case "make_call":
                    return CallTel.TriggerCall(search, context);
                case "directions":
                    return MapsIntent.GoogleMaps(search, context);
                case "play_music":
                    return MediaIntents.MusicPlayer(search, context);
                default:
                    return "";
            }


    }

}