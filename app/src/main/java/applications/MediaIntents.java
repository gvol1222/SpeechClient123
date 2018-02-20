package applications;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gvol on 25/10/2017.
 */

public class MediaIntents {


    public static String newYoutube(final String query, Context context) {

        String msg = null;
        if (query.equals("")) {
            msg = "Δεν υπάρχει βίντεο για ανεύρεση";
        } else {
            Intent intent = new Intent(Constatns.ACTION_SEARCH);
            intent.setFlags(Constatns.FLAGS);
            //intent.setPackage(Constatns.YputubePackage);
            intent.putExtra("query", query);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                msg = "Μετάβαση στο YouTube";
            }
        }
        return msg;
    }

    public static String MusicPlayer(final String query, Context context) {
        String msg = null;
        if (query.equals("")) {
            msg = "Αγνωστο Τραγούδι";
        } else {
            Intent intent = new Intent(Constatns.MUSIC_SEARCH);
            intent.setFlags(Constatns.FLAGS);
            intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*");
            intent.putExtra(SearchManager.QUERY, query);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }

        }
        return msg;
    }

    public static String TakeSelfie(Context context){
         final Uri mLocationForPhotos = null;

        PackageManager packageManager = context.getPackageManager();
        if(!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return "This device does not have a camera.";
        }
        // the intent is my camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //getting uri of the file
        Uri file = Uri.fromFile(getFile());
        intent.setFlags(Constatns.FLAGS);

        //Setting the file Uri to my photo
        intent.putExtra(MediaStore.EXTRA_OUTPUT,file);

        if(intent.resolveActivity(packageManager)!=null)
        {
            context.startActivity(intent);
        }

        return "";
    }
    private static File getFile() {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String mCurrentPhotoPath;
        //if it doesn't exist the folder will be created
        if(!folder.exists())
        {folder.mkdir();}

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+ timeStamp + "_";
        File image_file = null;

        try {
            image_file = File.createTempFile(imageFileName,".jpg",folder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (image_file != null) {
            mCurrentPhotoPath = image_file.getAbsolutePath();
        }
        return image_file;
    }

}
