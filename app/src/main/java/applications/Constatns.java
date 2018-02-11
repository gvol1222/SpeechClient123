package applications;

import android.content.Intent;
import android.provider.MediaStore;

/**
 * Created by bill on 11/9/17.
 */

public class Constatns {

    static final int FLAGS = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;
    static final String YputubePackage = "com.google.android.youtube";

    //app names
    static final String CALL_APP="make_call";
    static final String SEND_SMS="send_sms";


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
}
