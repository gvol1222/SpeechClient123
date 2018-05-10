package activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
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
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
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
    private ProgressBar progressBar;
    private ProgressBar WaitAction;
    private Toolbar toolbar;
    private ForeGroundRecognition speechService;
    private Intent speechintent;
    private boolean exit, assistantBound;
    private PulsatorLayout pulsator ;

    /*private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String result = intent.getStringExtra("result");

            Log.d(TAG, "onReceive msg : "+ result);

            if ( intent.getStringExtra("ripple")!=null &&  intent.getStringExtra("ripple").equals("ripple")) {

               pulsator.start();
            }else if(intent.getStringExtra("ripple")!=null &&  intent.getStringExtra("ripple").equals("ripple_stop")){
                pulsator.stop();
            }

            if (result!=null && !result.equals("")) {
                response.setText(result);
            }else if(result!=null) {
                response.setText("");
            }

        }
    };*/

    private ServiceConnection speechConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            ForeGroundRecognition.AssistantBinder binder = (ForeGroundRecognition.AssistantBinder) service;
            speechService = binder.getService();
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_gui);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        exit = sharedPref.getBoolean("exit", false);
        if (Build.VERSION.SDK_INT < 23) Init();
        pulsator = findViewById(R.id.pulsator);

        ButterKnife.bind(this);



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
                if (exit) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("exit", false);
                    editor.apply();
                    exit = false;
                    Toast.makeText(this, "Θα τερματίσει στη έξοδο", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Θα είναι πάντα ενεργό", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("exit", true);
                    editor.apply();
                    exit = true;
                }
                invalidateOptionsMenu();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        if (exit) {
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
        setProgress();
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

    private void setProgress() {
        //progressBar = findViewById(R.id.progressBar3);
        //WaitAction = findViewById(R.id.progressBar4);
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
        boolean btn = sharedPref.getBoolean(getResources().getString(R.string.switch_continuous), true);
        speechService.setContinuous(btn);
       if (AppPackagesUtils.isNetworkAvailable(this)) {
           Log.i(TAG,"boolean is"+b);
           if( speechService.isFinishedTts()) {

               if (b) {
               speechService.StartInteract();
               //showProgressBar();
                } else {

                   //clearProgressBar();
                   speechService.StopSrecognition();

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
        if (!exit) {
            stopService(speechintent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //EventBus.getDefault().register(this);
        //registerReceiver(broadcastReceiver, new IntentFilter(SpeechService.BroadcastAction));


    }

    @Override
    protected void onPause() {
        super.onPause();

      //  unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 7) {
            Init();
        }
    }

}
