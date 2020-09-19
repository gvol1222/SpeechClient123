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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;



import com.example.bill.Activities.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import activities.permission.PermissionActivity;
import activities.settings.SettingsActivity;
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
    private TextView response;
    private ToggleButton btnIput;
    private boolean paused;
    private Toolbar toolbar;
    private ForeGroundRecognition speechService;
    private Intent speechintent;
    private boolean exit, assistantBound;



    private ServiceConnection speechConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            ForeGroundRecognition.AssistantBinder binder = (ForeGroundRecognition.AssistantBinder) service;
            speechService = binder.getService();
            Log.i(TAG,"adf");
            assistantBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            assistantBound = false;
        }
    };



    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Events.PartialResults event ) {
        response.setText(event.getPartialResults());
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void isActivated(Events.ActivatedRecognition event ) {

       /* if(!event.isActivated() && !speechService.isContinuous()){
            btnIput.performClick();
            Constatns.app.Init();
        }*/

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        paused=false;
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_gui);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        exit = sharedPref.getBoolean("exit", true);
        Log.d("exit...", String.valueOf(exit));
        if (Build.VERSION.SDK_INT < 23) Init();


        ButterKnife.bind(this);



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

        if (speechService == null) {
            speechintent = new Intent(MainActivity.this, ForeGroundRecognition.class);
            speechintent.setAction("com.marothiatechs.foregroundservice.action.startforeground");

            startService(speechintent);
            bindService(speechintent, speechConnection, Context.BIND_AUTO_CREATE);
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

        setButtons();

        setText();
        setToolbar();
        setDrawerLayout();
        setNavigation();
        record();
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

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setButtons() {
        btnIput = findViewById(R.id.toggleButton2);
    }


    private void setText() {
        response = findViewById(R.id.textView2);
        response.setText("");
    }

    private void record() {


        btnIput.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                startRecord(b);

            }

        });


    }



    private void startRecord(boolean b) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean btn = sharedPref.getBoolean(getResources().getString(R.string.switch_continuous), false);
        //speechService.setContinuous(btn);
        if (AppPackagesUtils.isNetworkAvailable(this)) {
            Log.i(TAG,"boolean ias"+b);
            if( speechService.isFinishedTts()) {

                if (b) {

                    paused=true;
                    speechService.speak(getResources().getString(R.string.StartMessage),true);
                    //showProgressBar();
                } else {

                    //clearProgressBar();
                    speechService.StopSrecognition();
                    response.setText("");

                }

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
            stopService(speechintent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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