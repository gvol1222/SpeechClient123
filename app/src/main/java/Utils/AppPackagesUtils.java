package Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gvol on 11/16/17.
 */

public class AppPackagesUtils {

    public static String getpackagename(String query,Context context){
        HashMap<String,String> packages = getInstalledPackages(context);
        Log.d("Packages",packages.toString());
        List<String> pkglist =new ArrayList<>(packages.keySet());
        String matched;
        matched = SearchStringHelper.getBestStringMatch(pkglist,query).keySet().iterator().next();
        return packages.get(matched);

    }

    public static HashMap<String,String> getInstalledPackages(Context context){
        HashMap<String,String> list = new HashMap<String,String>();
        String TAG = "LIST";
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            list.put(packageInfo.loadLabel(pm).toString(),packageInfo.packageName);
        }
        return list;
    }
}

