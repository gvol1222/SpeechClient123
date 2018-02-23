package applications;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.math.MathUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import utils.jsonparsers.Entities;

/**
 * Created by gvol on 17/12/2017.
 */

public class Action implements Serializable {
    //Config Variables
    private String TAG = "ActionClass";

    /*Stage parms*/
    public String type;
    public boolean RequiresVerification;
    public boolean MultiStageCommFromStart;
    public boolean waiting_data;
    public String Current_Key;
    public String Stage;

    /*App Data */
    //Data for apps demanding content like sms
    public HashMap<String,String> data ;
    //Request data from user with message
    public HashMap<String,String> data_requests;
    /*Intents Parms*/

    public String IntentAction;
    public boolean RequiresUri;
    public boolean RequiresPackage;
    public boolean RequiresExtra;
    public Bundle extras;
    public Uri IntentURIprefix = Uri.EMPTY;
    public String UriQuery;
    public String Package ;

    public Entities entities;

    //Messages
    public String VERIFY_MESSAGE ;
    public String MULTI_STAGE_MESSAGE ;
    public String ACTION_FAILED ;
    public String NOT_FOUND ;
    public String LAUNCHED ;

    //Constructor
    public Action() {
        Init();
    }


    public void Init(){
        setIntentParms();
        setStageParms();
        setData();
        setMessages();


    }
    private void setMessages(){
        //Messages
        VERIFY_MESSAGE = "";
        ACTION_FAILED = "";
        NOT_FOUND = "";
        LAUNCHED = "";
        MULTI_STAGE_MESSAGE="";
    }
    private void setData(){
        //Data for apps demanding content like sms
        data = new HashMap<>();
        //Request data from user with message
        data_requests = new HashMap<>();
    }
    private void setStageParms(){
        type = "";
        RequiresVerification = false;
        MultiStageCommFromStart = false;
        waiting_data = false;
        Current_Key = "";
        Stage = Constatns.IN_STAGE;
    }
    private void setIntentParms(){
        IntentAction = null;
        IntentURIprefix = Uri.EMPTY;
        UriQuery = "";
        RequiresUri = false;
        RequiresPackage = false;
        Package ="";
        RequiresExtra =false;
        extras = new Bundle();
    }
    //Run the Intent
    public void runIntent(Context con) {

        if(type.equals(Constatns.SEND_SMS)){
            Stage = Constatns.CP_STAGE;
            Sms.SendMessage(data.get(Current_Key), con, data.get(Constatns.SMS_APP_NAME));
        }else  if(type.equals(Constatns.SET_REMINDER)){
            Stage = Constatns.CP_STAGE;
            String query = data.get(Constatns.REM_APP_NAME);
            Date datetime = utils.MathUtils.GetDtaeInfo(data.get(Constatns.REM_KEY_TIME));
            Log.i(TAG,datetime.toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(datetime);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int day = calendar.get(Calendar.DATE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            addReminderInCalendar(con,year,month,day,hour,minute,year,month,day,hour,minute,query);
            Log.d(TAG,"month "+month+" year "+year+" day "+day+" hour "+hour+" minute "+minute);
        }
        else {
            Stage = Constatns.CP_STAGE;
            Intent curIntent = CreateIntent();
            con.startActivity(curIntent);
        }

    }

    //Helper Classes
    private Intent CreateIntent() {
        Intent runInt;
        if (RequiresUri) {
            Log.d(TAG, "uri query: "+UriQuery);
            runInt = new Intent(IntentAction, Uri.parse(IntentURIprefix + Uri.encode(UriQuery, "UTF-8")));
        } else {
            runInt = new Intent(IntentAction);
        }
        runInt.setFlags(Constatns.FLAGS);
        if(RequiresPackage){
            Log.d(TAG, "package: "+Package);
            runInt.setPackage(Package);
        }/**/
        if(RequiresExtra){
            Log.d(TAG, "package: "+Package);
            runInt.putExtras(extras);
        }/**/
        return runInt;
    }

    private void addReminderInCalendar(Context con,int statrYear, int startMonth, int startDay, int startHour, int startMinut, int endYear, int endMonth, int endDay, int endHour, int endMinuts,String query) {

        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
        ContentResolver cr = con.getContentResolver();
        TimeZone timeZone = TimeZone.getDefault();

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(statrYear, startMonth, startDay, startHour, startMinut);
        long startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(endYear, endMonth, endDay, endHour, endMinuts);
        long endMillis = endTime.getTimeInMillis();

        /** Inserting an event in calendar. */
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.TITLE, query);
        values.put(CalendarContract.Events.DESCRIPTION, query);
        values.put(CalendarContract.Events.ALL_DAY, 0);
        // event starts at 11 minutes from now
        values.put(CalendarContract.Events.DTSTART, startMillis);
        // ends 60 minutes from now
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        Uri event = cr.insert(EVENTS_URI, values);

        // Display event id.
        Log.i(TAG, "Event added :: ID :: " + event.getLastPathSegment());

        /** Adding reminder for event added. */
        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
        values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, 10);

        cr.insert(REMINDERS_URI, values);
    }

    /** Returns Calendar Base URI, supports both new and old OS. */
    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }
}