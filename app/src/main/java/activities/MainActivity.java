package activities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.bill.Activities.R;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import activities.permission.PermissionActivity;
import activities.settings.SettingsActivity;
import applications.Message;
import applications.MessageAdapter;
import butterknife.ButterKnife;
import events.Events;
import recogniton_service.ForeGroundRecognition;
import utils.AppPackagesUtils;

/**
 * Created by bill on 11/20/17.
 */

@SuppressLint("Registered")
public class MainActivity extends PermissionActivity implements NavigationView.OnNavigationItemSelectedListener {

    // private ResponseReceiver receiver;
    private static final String TAG = "BtroadCast";
    SharedPreferences sharedPref;

    private Toolbar toolbar;
    private Button btn;
    private ForeGroundRecognition foreGroundRecognition;
    private Intent SpeechIntent;
    private boolean exit;
    LottieAnimationView toggle;
    boolean flag = false;
    private boolean isClicked;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private ServiceConnection speechConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            ForeGroundRecognition.AssistantBinder binder = (ForeGroundRecognition.AssistantBinder) service;
            foreGroundRecognition = binder.getService();
            Log.i(TAG, "adf");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Events.Results event) {
        System.out.print(event.getResults());
        Message msg = new Message(event.getResults(),true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.i("main  res",event.getResults());
                messageAdapter.add(msg);
                messagesView.setSelection(messagesView.getCount() - 1);


            }
        });
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void SpeechMessageShow(Events.SpeechMessageShow message) {

        Message msg = new Message(message.getSpeechMessage(),false);

        messageAdapter.add(msg);
        messagesView.setSelection(messagesView.getCount() - 1);

    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Events.PartialResults event) {

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void isActivated(Events.ActivatedRecognition event )
    {
        if(event.isActivated()) {


            toggle.setAnimation("pause.json");
            toggle.setRepeatCount(LottieDrawable.INFINITE);
            toggle.playAnimation();

        }
        else
        {
            toggle.cancelAnimation();
            toggle.setAnimation("play_button.json");
            toggle.clearAnimation();


        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_gui);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        exit = sharedPref.getBoolean("exit", true);
        Log.d("exit...", String.valueOf(exit));
        Init();
        ButterKnife.bind(this);
        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        isClicked = true;
        toggle = findViewById(R.id.lav_toggle);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (flag == false) {
                    flag = true;
                    startRecord(flag);

                    //---- Your code here------
                } else {
                    toggle.cancelAnimation();
                    toggle.clearAnimation();
                    toggle.setAnimation("play_button.json");

                    flag = false;
                    startRecord(flag);

                    //---- Your code here------
                }


            }
        });

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "1",
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (foreGroundRecognition == null) {
            SpeechIntent = new Intent(MainActivity.this, ForeGroundRecognition.class);
            SpeechIntent.setAction("com.marothiatechs.foregroundservice.action.startforeground");
            startService(SpeechIntent);
            bindService(SpeechIntent, speechConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;

        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.d("exit...1", String.valueOf(exit));
                if (!exit) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("exit", true);
                    editor.apply();
                    exit = true;
                    Toast.makeText(this, "Θα τερματίσει στη έξοδο", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Θα είναι πάντα ενεργό", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("exit", false);
                    editor.apply();
                    exit = false;
                }
                Log.d("exit...2", String.valueOf(exit));
                invalidateOptionsMenu();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!exit) {
            menu.findItem(R.id.action_settings)
                    .setIcon(R.mipmap.power_off);
        } else {
            menu.findItem(R.id.action_settings)
                    .setIcon(R.mipmap.power_on);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_action) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this, SettingsActivity.class));

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Init() {
        setGui();
        setDrawerLayout();
        setNavigation();
    }

    //set gui functions
    private void setNavigation() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setDrawerLayout() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setGui() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
      //  btn = findViewById(R.id.button2);

    }


    private void startRecord(boolean b) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean btn = sharedPref.getBoolean(getResources().getString(R.string.switch_continuous), false);
        //speechService.setContinuous(btn);

        if (AppPackagesUtils.isNetworkAvailable(this)) {
            Log.i(TAG,"boolean ias"+b);


            if (b) {


                foreGroundRecognition.speak(getResources().getString(R.string.StartMessage));

            } else {


                foreGroundRecognition.StopSrecognition();


            }


        }else {
            Toast.makeText(this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbindService(speechConnection);
        exit = sharedPref.getBoolean("exit", false);
        Log.d("exit...", String.valueOf(exit));
        if (exit) {
            stopService(SpeechIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 7) {
            Init();
        }
    }

}