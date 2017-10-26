package com.example.bill.speechclient;

import android.content.Context;
import android.net.Uri;

/**
 * Created by bill on 10/26/17.
 */

public class CallTel extends LaunchApp {

    public CallTel(String action, Context context) {
        super(action, context);
    }


    public CallTel(Context context) {
        super(context);
    }

    @Override
    public void AddFlag(int flag) {
        super.AddFlag(flag);
    }

    @Override
    public void AddExtra(String name, String extra) {
        super.AddExtra(name, extra);
    }

    @Override
    public void setData(Uri uri) {
        super.setData(uri);
    }

    @Override
    public void SetData(String PackageName) {
        super.SetData(PackageName);
    }

    @Override
    public void TriggerIntent() {
        super.TriggerIntent();
    }
}
