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
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
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
                    boolean SendSms = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean WritePermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadPermission = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean CameraPermission = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadCalendar = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteCalendar = grantResults[8] == PackageManager.PERMISSION_GRANTED;
                    if (ReadContacts && CallPhone && RecordAudio && SendSms && WritePermission && ReadPermission && CameraPermission && ReadCalendar && WriteCalendar ) {
                        Toast.makeText(this, "Activities.Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Activities.Permission Deinied", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }



}



