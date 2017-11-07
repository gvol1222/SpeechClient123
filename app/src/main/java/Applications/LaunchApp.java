package Applications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by bill on 10/25/17.
 */

public abstract class LaunchApp {

    private Intent IntentApp;
    private Context context;

    public LaunchApp( String action,Context context) {
        this.context = context;
        IntentApp = new Intent(action);

    }

    public LaunchApp(Context context) {
        this.context = context;
        IntentApp = new Intent();

    }



    public void AddFlag(int flag){
        IntentApp.setFlags(flag);
    }
    public void AddExtra(String name,String extra){
        IntentApp.putExtra(name,extra);
    }



    public void setData(Uri uri){
        IntentApp.setData(uri);
    }
    public void SetData(String PackageName){
            IntentApp.setPackage(PackageName);

    }

    public void TriggerIntent(){
        if (IntentApp.resolveActivity(context.getPackageManager()) != null)
        context.startActivity(IntentApp);
    }

}
