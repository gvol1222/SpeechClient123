package applications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import utils.ContactUtils;

/**
 * Created by gvol on 28/1/2018.
 */

public class Switcher {


    private static String TAG = "Switcher";
    public static Action selectActionbyType(Action app, String type) {

        Log.i(TAG,"type of app is: "+app.type);

        if (type.equals(Constatns.CALL_APP)) {
            app = InitActionObj(
                    app,type,Constatns.ACTION_CALL,true,"contact",
                    "Πείτε μου το όνομα της επαφής ή αριθμό τηλεφώνου","tel:",
                    false,
                    "Η επαφή δεν βρέθηκε.",Constatns.CH_STAGE,"Επιθυμείτε να πραγματοποιηθεί το τηλεφώνημα"
            );
        }else if(type.equals(Constatns.SEND_SMS)){
            app = InitActionObj(
                    app,type,"SMS",false,"contact",
                    "Πείτε μου το όνομα της επαφής ή αριθμό τηλεφώνου","",
                    false,
                    "Η επαφή δεν βρέθηκε.",Constatns.CH_STAGE,"Επιθυμείτε να αποσταλεί το μήνυμα"
            );
        }
        return app;
    }

    public static Action transforminfo (Action app, Context con){

        if (app.type.equals(Constatns.CALL_APP)){

            String query = app.data.get("contact");

            if(app.entities.getPhoneNumber()!=null || app.entities.getNumber()!=null){

                Log.i(TAG,"found number ");
                app.data.put("contact", query);
                app.UriQuery = app.data.get("contact");
                app.Stage = Constatns.VR_STAGE;

            }else if (ContactUtils.ContactNumber(query,con).size() > 0 ) {
                Log.i(TAG,"found name ");
                app.data.put("contact", ContactUtils.ContactNumber(app.data.get("contact"), con).get(0));
                app.UriQuery = app.data.get("contact");
                app.Stage = Constatns.VR_STAGE;
            }
            else {
                Log.i(TAG,"not found tel ");
                app.Stage = Constatns.NF_STAGE;
            }
        }else if(app.type.equals(Constatns.SEND_SMS)){

            String query = app.data.get("contact");

            if(app.entities.getPhoneNumber()!=null || app.entities.getNumber()!=null){
                Log.i(TAG,"found number ");
                app.data.put("contact", query);
                app.Stage = "multifromStart";
                app.Current_Key ="SMS";
                app.data_requests.put("SMS","Πείτε μου το κείμενο που επιθυμείτε να στείλετε");

            }else if (ContactUtils.ContactNumber(query,con).size() > 0 ) {
                app.data.put("contact", ContactUtils.ContactNumber(app.data.get("contact"), con).get(0));
                app.Stage = "multifromStart";
                app.Current_Key ="SMS";
                app.data_requests.put("SMS","Πείτε μου το κείμενο που επιθυμείτε να στείλετε");
            }
            else {
                Log.i(TAG,"not found tel ");
                app.Stage = Constatns.NF_STAGE;
            }
        }

        return app;
    }



    private static Action InitActionObj(Action app,String type,String IntentAction,boolean requiresUri,
                                        String AppName,String SoundMessage,String uri,
                                        boolean MultiStageCommFromStart,String NOT_FOUND,String Stage,String VerifyMessage
    ){

        app.type = type;
        app.IntentAction = IntentAction;
        app.RequiresUri = requiresUri;
        app.data.put(AppName, null);
        app.data_requests.put(AppName, SoundMessage);
        app.Current_Key = AppName;
        app.IntentURIprefix = Uri.parse(uri);
        app.MultiStageCommFromStart = MultiStageCommFromStart;
        app.NOT_FOUND = NOT_FOUND;
        app.Stage = Stage;
        app.VERIFY_MESSAGE =VerifyMessage;

        return app;

    }


}