package applications.unique_apps;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import androidx.annotation.RequiresApi;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedHashMap;

import applications.Constants;
import events.Events;

/**
 * Created by CimaDev1 on 2/28/2018.
 */

public class UniqueSwitcher {


    private static final String TAG ="UniqueSwitcher";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void Switcher(String type, LinkedHashMap data, Context con){

        if(type.equals(Constants.SET_REMINDER)){
            HashMap<String,Integer> datetime = utils.MathUtils.GetDtaeInfo((String) data.get(Constants.REM_KEY_TIME));


           if(datetime!=null){
               String query = (String) data.get(Constants.REM_APP_NAME);

               int month = datetime.get("month");
               int year = datetime.get("year");
               int day =datetime.get("day");
               int hour = datetime.get("hour");
               int minute = datetime.get("minute");

               Reminder.addReminderInCalendar(con,year,month,day,hour,minute,year,month,day,hour,minute,query);
               Log.d(TAG,"month "+month+" year "+year+" day "+day+" hour "+hour+" minute "+minute);
               Constants.app.Stage = Constants.CP_STAGE;
           }else
           {
               Constants.app.Stage = Constants.NF_STAGE;
               EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
           }

        }else if(type.equals(Constants.SET_ALARM)){

            HashMap<String,Integer> datetime = utils.MathUtils.GetDtaeInfo((String) data.get(Constants.ALARM_DATE_TIME));


            if(datetime!=null)
            {
                int hour = datetime.get("hour");
                int minute = datetime.get("minute");

                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                        .putExtra(AlarmClock.EXTRA_HOUR, hour)
                        .putExtra(AlarmClock.EXTRA_MINUTES, minute)
                        .putExtra(AlarmClock.EXTRA_SKIP_UI,true);
                intent.setFlags(Constants.FLAGS);
                if (intent.resolveActivity(con.getPackageManager()) != null) {
                    con.startActivity(intent);
                }
                Constants.app.Stage = Constants.CP_STAGE;
            }
            else
            {
                Constants.app.Stage = Constants.NF_STAGE;
                EventBus.getDefault().postSticky(new Events.ActivatedRecognition(false));
            }

        }
        else if(type.equals(Constants.SEND_SMS)){
            Sms.SendMessage((String)data.get(Constants.SMS_CONTENT_NAME), con, (String)data.get(Constants.SMS_APP_NAME));
            Constants.app.Stage = Constants.CP_STAGE;
        }else if(type.equals(Constants.OPEN_APP)){
            String query = (String) data.get(Constants.OPEN_APP_NAME);
            Constants.app.Stage=LaunchApp.launchapplication(query, con);
        }


    }


}