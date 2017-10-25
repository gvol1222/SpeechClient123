package com.example.bill.speechclient;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by gvol on 25/10/2017.
 */

public class youtube {

    public static void get (Context con, String query){

        try {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            Log.d("APPKind", "Trying new intent");
            intent.setPackage("com.google.android.youtube");
            intent.putExtra("query", query);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            con.startActivity(intent);
        }
        catch (Exception e){
            Toast.makeText(con,"OOPS! Something's Bad!", Toast.LENGTH_SHORT).show();
        }

    }
}
