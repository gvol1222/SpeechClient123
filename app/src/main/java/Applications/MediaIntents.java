package Applications;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * Created by gvol on 25/10/2017.
 */

public class MediaIntents {


    public static String newYoutube(final String query, Context context) {

        String msg = null;
        if (query.equals("")) {
            msg = "Δεν υπάρχει βίντεο για ανεύρεση";
        } else {
            Intent intent = new Intent(Constatns.actionSearch);
            intent.setFlags(Constatns.flag);
            intent.setPackage(Constatns.YputubePackage);
            intent.putExtra("query", query);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                msg = "Μετάβαση στο YouTube";
            }
        }
        return msg;
    }

    public static String MusicPlayer(final String query, Context context) {
        String msg = null;
        if (query.equals("")) {
            msg = "Αγνωστο Τραγούδι";
        } else {
            Intent intent = new Intent(Constatns.MusicSearch);
            intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*");
            intent.putExtra(SearchManager.QUERY, query);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }

        }
        return msg;
    }


}
