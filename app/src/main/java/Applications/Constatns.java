package Applications;

import android.content.Intent;
import android.provider.MediaStore;

/**
 * Created by bill on 11/9/17.
 */

public class Constatns {

    public static final int flag = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;

    public static final String YputubePackage = "com.google.android.youtube";
    //actions
    public static final String actionCall = Intent.ACTION_CALL;
    public static final String actionSearch = Intent.ACTION_SEARCH;
    public static final  String MusicSearch = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH;
}
