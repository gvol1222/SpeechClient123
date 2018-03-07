package applications.unique_apps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.bill.Activities.R;

import applications.Constatns;
import utils.AppPackagesUtils;

/**
 * Created by gvol on 11/16/17.
 */

class LaunchApp  {

    static String launchapplication(String qry, Context con) {
        String packagename = AppPackagesUtils.getpackagename(qry,con);
        String TAG = "LaunchApp";
        PackageManager manager;
        manager = con.getPackageManager();
        Intent i;

        if (!packagename.equals(AppPackagesUtils.NO_MATCH)) {
            Log.i(TAG, "opening app successful " + qry);
            String appName = "";
            try {
                appName = (String) manager.getApplicationLabel(manager.getApplicationInfo(packagename, PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            try {
                i = manager.getLaunchIntentForPackage(packagename);
                if (i != null) {
                    Constatns.app.LAUNCHED = Constatns.app.LAUNCHED+" "+appName;
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