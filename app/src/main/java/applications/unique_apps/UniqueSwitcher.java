package applications.unique_apps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.provider.AlarmClock;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import applications.Constatns;

/**
 * Created by CimaDev1 on 2/28/2018.
 */

public class UniqueSwitcher {


    private static final String TAG ="UniqueSwitcher";
    public static void Switcher(String type, LinkedHashMap data, Context con){

        if(type.equals(Constatns.SET_REMINDER)){
            HashMap<String,Integer> datetime = utils.MathUtils.GetDtaeInfo((String) data.get(Constatns.REM_KEY_TIME));
            String query = (String) data.get(Constatns.REM_APP_NAME);

            int month = datetime.get("month");
            int year = datetime.get("year");
            int day =datetime.get("day");
            int hour = datetime.get("hour");
            int minute = datetime.get("minute");

            Reminder.addReminderInCalendar(con,year,month,day,hour,minute,year,month,day,hour,minute,query);
            Log.d(TAG,"month "+month+" year "+year+" day "+day+" hour "+hour+" minute "+minute);
        }else if(type.equals(Constatns.SET_ALARM)){
            HashMap<String,Integer> datetime = utils.MathUtils.GetDtaeInfo((String) data.get(Constatns.ALARM_DATE_TIME));
            int month = datetime.get("month");
            int year = datetime.get("year");
            int day =datetime.get("day");
            int hour = datetime.get("hour");
            int minute = datetime.get("minute");

            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                    .putExtra(AlarmClock.EXTRA_HOUR, hour)
                   .putExtra(AlarmClock.EXTRA_MINUTES, minute)
                    .putExtra(AlarmClock.EXTRA_SKIP_UI,true);
            intent.setFlags(Constatns.FLAGS);
            if (intent.resolveActivity(con.getPackageManager()) != null) {
                con.startActivity(intent);
            }

            /*List<ResolveInfo> resolveInfos = con.getPackageManager().queryIntentActivities(intent, 0); // returns all applications which can listen to the SEND Intent
            for (ResolveInfo info : resolveInfos) {
                ApplicationInfo applicationInfo = info.activityInfo.applicationInfo;

                //get package name, icon and label from applicationInfo object and display it in your custom layout
                //App icon = applicationInfo.loadIcon(pm);
                String name  = applicationInfo.loadLabel(con.getPackageManager()).toString();
                Log.d(TAG,"app name "+name);
                String ppackage_name = applicationInfo.packageName;
                Log.d(TAG,"app name "+ppackage_name);
            }
            Log.d(TAG,"month "+month+" year "+year+" day "+day+" hour "+hour+" minute "+minute);*/
        }
        else if(type.equals(Constatns.SEND_SMS)){
            Sms.SendMessage((String)data.get(Constatns.SMS_CONTENT_NAME), con, (String)data.get(Constatns.SMS_APP_NAME));
        }else if(type.equals(Constatns.OPEN_APP)){
            String query = (String) data.get(Constatns.OPEN_APP_NAME);
            Constatns.app.Stage=LaunchApp.launchapplication(query, con);
        }


    }


}