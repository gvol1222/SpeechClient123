package applications;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.bill.Activities.R;

import utils.AppPackagesUtils;

/**
 * Created by gvol on 11/16/17.
 */

class LaunchApp {

    static String launchapplication(String qry, Context con) {
        String packagename = AppPackagesUtils.getpackagename(qry,con);
        String TAG = "LaunchApp";
        PackageManager manager;
        manager = con.getPackageManager();
        Intent i;

        if (!packagename.equals(AppPackagesUtils.NO_MATCH)) {
            Log.i(TAG, "opening app successful " + qry);
            try {
                i = manager.getLaunchIntentForPackage(packagename);
                if (i != null) {
                    i.setFlags(Constatns.FLAGS);
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    con.startActivity(i);
                } else {
                    throw new PackageManager.NameNotFoundException();

                }
            }catch (PackageManager.NameNotFoundException e){
                Log.i(TAG, "something goes wrong " + e);

            }


            return Constatns.CP_STAGE;
        } else {
            Log.i(TAG, "app does not exist " + qry);
            return Constatns.NF_STAGE;

        }


    }
}
