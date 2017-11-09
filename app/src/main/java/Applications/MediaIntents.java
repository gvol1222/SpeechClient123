package Applications;

import android.content.Context;
import android.content.Intent;

/**
 * Created by gvol on 25/10/2017.
 */

public class MediaIntents {


    public static String newYoutube(final String query, Context context) {

        if (query.equals("")) {
            return "Δεν υπάρχει βίντεο για ανεύρεση";
        } else {
            final Intent intent = new Intent(Constatns.actionSearch);
            intent.setFlags(Constatns.flag);
            intent.setPackage(Constatns.YputubePackage);
            intent.putExtra("query", query);
            if (intent.resolveActivity(context.getPackageManager()) != null)
                context.startActivity(intent);
            return "Μετάβαση στο YouTube";
        }
    }


}
