package applications;

import android.content.Intent;
import android.provider.MediaStore;

/**
 * Created by bill on 11/9/17.
 */

public class Constatns {

    static final int FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
    static final String YputubePackage = "com.google.android.youtube";
    public  static final Action app = new Action();

    //action receivers
    public static final String NOT_ACTION="notification.action";
    public static final String MAESTRO_ACTION="speak.action";
    //app names
    static final String CALL_APP="make_call";
    static final String SEND_SMS="send_sms";
    static final String OPEN_APP="open_app";
//    /static final String OPEN_APP="open_app";
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

    //actions
    static final String ACTION_CALL = Intent.ACTION_CALL;
    public static final String ACTION_SEARCH = Intent.ACTION_SEARCH;
    public static final  String MUSIC_SEARCH = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH;

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
    public static final String SMS_NOT_FOUND_MESSAGE ="Η επαφή δεν βρέθηκε.";
    public static final String SMS_VER_MESSAGE ="Επιθυμείτε να αποσταλεί το μήνυμα";
    public static final String SMS_APP_NAME ="contact";

    //send sms tel
    public static final String OPEN_INFO_MESSAGE ="Πείτε μου το όνομα της εφαρμογής";
    public static final String OPEN_URI ="";
    public static final String OPEN_NOT_FOUND_MESSAGE ="Η εφαρμογη δεν βρέθηκε";
    public static final String OPEN_CONTENT_MESSAGE ="Πείτε μου το κείμενο που επιθυμείτε να στείλετε";
    public static final String OPEN_VER_MESSAGE ="";
    public static final String OPEN_APP_NAME ="app_query";
}
