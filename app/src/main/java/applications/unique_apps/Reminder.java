package applications.unique_apps;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by CimaDev1 on 2/28/2018.
 */

public class Reminder {


    private static final String TAG = "Reminder";
    protected static void addReminderInCalendar(Context con, int statrYear, int startMonth, int startDay, int startHour, int startMinut,
                                                int endYear, int endMonth, int endDay, int endHour, int endMinuts, String query) {

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
       // Log.i(TAG, "Event added :: ID :: " + event.getLastPathSegment());

        /** Adding reminder for event added. */
        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
        values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, 10);

        cr.insert(REMINDERS_URI, values);

    }

    /** Returns Calendar Base URI, supports both new and old OS. */
    private static String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                    .parse("content://com.android.calendar/calendars");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert calendarURI != null;
        return calendarURI.toString();
    }
}