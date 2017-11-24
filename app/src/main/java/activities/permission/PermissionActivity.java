package activities.permission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by bill on 11/21/17.
 */

public abstract class PermissionActivity extends AppCompatActivity {

    private static final int RequestPermissionCode = 7;
    private static final String[] permissions = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(Build.VERSION.SDK_INT < 23)) {
            ActivityCompat.requestPermissions(this, permissions, RequestPermissionCode);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case RequestPermissionCode:

                if (grantResults.length > 0) {
                    boolean ReadContacts = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean CallPhone = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordAudio = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (ReadContacts && CallPhone && RecordAudio) {
                        Toast.makeText(this, "Activities.Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Activities.Permission Deinied", Toast.LENGTH_LONG).show();
                    }

                }
                break;
        }
    }


}



