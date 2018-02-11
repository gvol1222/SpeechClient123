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

        if (!packagename.equals(AppPackagesUtils.NO_MATCH)) {
            Log.i(TAG, "opening app successful " + qry);
            Intent i = manager.getLaunchIntentForPackage(packagename);
            if (i != null) {
                i.setFlags(Constatns.FLAGS);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
            } else {
                Log.i(TAG, "something goes wrong " + qry);

            }

            con.startActivity(i);
            return con.getResources().getString(R.string.success_open) + qry;
        } else {
            Log.i(TAG, "app does not exist " + qry);
            return con.getResources().getString(R.string.no_success_open);

        }


    }
}
