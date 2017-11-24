package applications;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by gvol on 12/11/2017.
 */

public class MapsIntent {

    public static String GoogleMaps(String query, Context context){
        Uri mapuri = Uri.parse("google.navigation:q="+Uri.encode(query,"UTF-8"));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapuri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
        return "Αρχίζει η καθοδήγηση";

    }
}
