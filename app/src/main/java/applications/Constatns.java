package applications;

import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

/**
 * Created by bill on 11/9/17.
 */

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Constatns {

    public static final int FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP;
    public  static final Action app = new Action();

    static final String YOUTUBE_PACKAGE = "com.google.android.youtube";
    static final String MAPS_PACKAGE = "com.google.android.apps.maps";

    //action receivers
    public static final String NOT_ACTION="notification.action";
    public static final String MAESTRO_ACTION="speak.action";
    //app names
    static final String CALL_APP="make_call";
    public static final String SEND_SMS="send_sms";
    public static final String OPEN_APP="open_app";
    static final  String DIRECTIONS="directions";
    static final String PLAY_VIDEO="play_video";
    static final String PLAY_MUSIC="play_music";
    public static final String SET_REMINDER="set_reminder";
    public static final String SET_ALARM="set_alarm";
    public static final String SEARCH_GOOGLE="search_google";
    public static final String SET_TIMER="set_timer";
    //actions
    static final String ACTION_CALL = Intent.ACTION_CALL;
    public static final String ACTION_SEARCH = Intent.ACTION_SEARCH;
    public static final  String MUSIC_SEARCH = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH;
    public static final  String ACTION_ALARM = AlarmClock.ACTION_SET_ALARM;
    public static final  String ACTION_TIMER = AlarmClock.ACTION_SET_TIMER;
    public static final  String TIMER_EXTRA = AlarmClock.EXTRA_LENGTH;

    //static final String OPEN_APP="open_app";
    //stages

    public static final String IN_STAGE ="IN";
    public static final String CH_STAGE ="CH";
    public static final String TR_STAGE ="TR";
    public static final String VR_STAGE ="VR";
    public static final String CP_STAGE ="CP";
    public static final String NF_STAGE ="NF";
    public static final String RUN_STAGE ="RS";
    public static final String NO_SPEACH_STAGE ="NS";
    public static final String MULTI_COMMAND_FROM_START ="MCFS";
    public static final String AFTER_VR_STAGE ="AFV";



    //messages

    //call tel
    public static final String CALL_INFO_MESSAGE ="Πείτε μου το όνομα της επαφής ή αριθμό τηλεφώνου";
    public static final String CALL_URI ="tel:";
    public static final String CALL_NOT_FOUND_MESSAGE ="Η επαφή δεν βρέθηκε.";
    public static final String CALL_VER_MESSAGE ="Επιθυμείτε να πραγματοποιηθεί το τηλεφώνημα";
    public static final String CALL_APP_NAME ="contact";

    //send sms tel
    public static final String SMS_INFO_MESSAGE ="Πείτε μου το όνομα της επαφής ή αριθμό τηλεφώνου";
    public static final String SMS_URI ="";
    public static final String SMS_CONTENT_MESSAGE ="Πείτε μου το κείμενο που επιθυμείτε";
    public static final String SMS_NOT_FOUND_MESSAGE ="Η επαφή δεν βρέθηκε.";
    public static final String SMS_VER_MESSAGE ="Επιθυμείτε να αποσταλεί το μήνυμα";
    public static final String SMS_APP_NAME ="sms_contact";
    public static final String SMS_CONTENT_NAME ="sms_content";
    //send sms tel
    public static final String OPEN_INFO_MESSAGE ="Πείτε μου το όνομα της εφαρμογής";
    public static final String OPEN_URI ="";
    public static final String OPEN_NOT_FOUND_MESSAGE ="Η εφαρμογη δεν βρέθηκε";
    public static final String OPEN_CONTENT_MESSAGE ="";
    public static final String OPEN_VER_MESSAGE ="";
    public static final String OPEN_APP_NAME ="app_query";
    public static final String OPEN_LAUNCHED_MESSAGE ="Ανοίγει η εφαρμογή";

    public static final String MAPS_INFO_MESSAGE ="Πείτε μου τη τοποθεσία που επιθυμείτε";
    public static final String MAPS_URI ="google.navigation:q=";
    public static final String MAPS_NOT_FOUND_MESSAGE ="Η τοποθεσία δεν βρέθηκε";
    public static final String MAPS_CONTENT_MESSAGE ="";
    public static final String MAPS_VER_MESSAGE ="";
    public static final String MAPS_APP_NAME ="maps_query";
    public static final String MAPS_LAUNCHED_MESSAGE ="Οδηγίες για τη τοποθεσία";


    public static final String VIDEO_INFO_MESSAGE ="Πείτε μου τo βίντεο που επιθυμείτε";
    public static final String VIDEO_URI ="";
    public static final String VIDEO_NOT_FOUND_MESSAGE ="Το βίντεο δεν βρέθηκε";
    public static final String VIDEO_CONTENT_MESSAGE ="";
    public static final String VIDEO_VER_MESSAGE ="";
    public static final String VIDEO_APP_NAME ="video_query";
    public static final String VIDEO_EXTRA ="query";
    public static final String VIDEO_LAUNCHED_MESSAGE ="Ανοίγει το youtube";

    public static final String MUSIC_INFO_MESSAGE ="Πείτε μου το μουσικό κομμάτι που επιθυμείτε";
    public static final String MUSIC_URI ="";
    public static final String MUSIC_NOT_FOUND_MESSAGE ="Το  μουσικό κομμάτι δεν βρέθηκε";
    public static final String MUSIC_CONTENT_MESSAGE ="";
    public static final String MUSIC_VER_MESSAGE ="";
    public static final String MUSIC_APP_NAME ="music_query";
    public static final String MUSIC_EXTRA ="vnd.android.cursor.item/*";
    public static final String MUSIC_LAUNCHED_MESSAGE ="Ανοίγει το μουσικό κομμάτι";

    public static final String REM_TIME_MESSAGE ="Πείτε μου πότε θέλετε υπενθύμιση";
    public static final String REM_URI ="";
    public static final String REM_NOT_FOUND_MESSAGE ="";
    public static final String REM_CONTENT_MESSAGE ="Πείτε μου τίτλο για την υπενθύμιση";
    public static final String REM_VER_MESSAGE ="Επιθυμείτε να προστεθεί η υπενθύμιση";
    public static final String REM_KEY_TIME ="rem_time";
    public static final String REM_APP_NAME ="rem_query";
    public static final String REM_DATE_TIME ="date_time";
    public static final String REM_EXTRA_BEGIN_TIME ="";
    public static final String REM_SUCCESS_MESSAGE ="Η υπενθύμιση προγραμματίστηκε ";


    public static final String ALARM_TIME_MESSAGE ="Πείτε μου πότε θέλετε ξυπνητήρι";
    public static final String ALARM_URI ="";
    public static final String ALARM_NOT_FOUND_MESSAGE ="";
    public static final String ALARM_CONTENT_MESSAGE ="";
    public static final String ALARM_VER_MESSAGE ="";
    public static final String ALARM_KEY_TIME ="alrm_time";
    public static final String ALARM_APP_NAME ="alarm";
    public static final String ALARM_DATE_TIME ="rem_time";
    public static final String ALARM_EXTRA_BEGIN_TIME ="";
    public static final String ALARM_SUCCESS_MESSAGE ="To ξυπνητήρι ορίστικε";

    public static final String GOOGLE_SEARCH_INFO_MESSAGE ="Πείτε μου τι θέλετε να ψάξω";
    public static final String GOOGLE_SEARCH_URI ="https://www.google.com/search?q=";
    public static final String GOOGLE_SEARCH_NOT_FOUND_MESSAGE ="";
    public static final String GOOGLE_SEARCH_APP_NAME ="query_search";
    public static final String GOOGLE_SEARCH_SUCCESS_MESSAGE ="Ανοίγει το google search";

    public static final String TIMER_INFO_MESSAGE ="Πείτε για πόση ώρα να μετρήσω";
    public static final String TIMER_URI ="";
    public static final String TIMER_NOT_FOUND_MESSAGE ="";
    public static final String TIMER_KEY ="timer_time";
    public static final String TIMER_SUCCESS_MESSAGE ="Ξεκινάει η χρονομέτρηση";
}