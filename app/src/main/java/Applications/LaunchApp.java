package Applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.util.Log;

import Utils.AppPackagesUtils;
import Utils.ApplicationUtils;

/**
 * Created by gvol on 11/16/17.
 */

public class LaunchApp {

    public static String launchapplication(String qry, Context con){
        String packagename = AppPackagesUtils.getpackagename(qry,con);
        Log.d("Selected package",packagename);
        Intent i = new Intent();
        PackageManager manager;
        manager = con.getPackageManager();
        try {
            i = manager.getLaunchIntentForPackage(packagename);
            if (i == null)
                throw new PackageManager.NameNotFoundException();
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            con.startActivity(i);
            return "Ανοίγει η εφαρμογή: " + qry;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Eeeee","Something went Wrong");
            e.printStackTrace();
            return "Κάτι πήγε στραβά!";
        }


    }
}
