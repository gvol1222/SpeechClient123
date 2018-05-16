package applications.unique_apps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.AlarmClock;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import applications.Constatns;
import events.Events;

/**
 * Created by CimaDev1 on 2/28/2018.
 */

public class UniqueSwitcher {


    private static final String TAG ="UniqueSwitcher";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void Switcher(String type, LinkedHashMap data, Context con){

        if(type.equals(Constatns.SET_REMINDER)){
            HashMap<String,Integer> datetime = utils.MathUtils.GetDtaeInfo((String) data.get(Constatns.REM_KEY_TIME));


           if(datetime!=null){
               String query = (String) data.get(Constatns.REM_APP_NAME);

               int month = datetime.get("month");
               int year = datetime.get("year");
               int day =datetime.get("day");
               int hour = datetime.get("hour");
               int minute = datetime.get("minute");

               Reminder.addReminderInCalendar(con,year,month,day,hour,minute,year,month,day,hour,minute,query);
               Log.d(TAG,"month "+month+" year "+year+" day "+day+" hour "+hour+" minute "+minute);
               Constatns.app.Stage = Constatns.CP_STAGE;
           }else
           {
               Constatns.app.Stage = Constatns.NF_STAGE;
               EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
           }

        }else if(type.equals(Constatns.SET_ALARM)){

            HashMap<String,Integer> datetime = utils.MathUtils.GetDtaeInfo((String) data.get(Constatns.ALARM_DATE_TIME));


            if(datetime!=null)
            {
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
                Constatns.app.Stage = Constatns.CP_STAGE;
            }
            else
            {
                Constatns.app.Stage = Constatns.NF_STAGE;
                EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
            }

        }
        else if(type.equals(Constatns.SEND_SMS)){
            Sms.SendMessage((String)data.get(Constatns.SMS_CONTENT_NAME), con, (String)data.get(Constatns.SMS_APP_NAME));
            Constatns.app.Stage = Constatns.CP_STAGE;
        }else if(type.equals(Constatns.OPEN_APP)){
            String query = (String) data.get(Constatns.OPEN_APP_NAME);
            Constatns.app.Stage=LaunchApp.launchapplication(query, con);
        }


    }


}