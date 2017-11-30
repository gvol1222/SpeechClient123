package activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.bill.Activities.R;

import activities.permission.PermissionActivity;
import activities.settings.SettingsActivity;
import recogniton_service.ForeGroundRecognition;
import recogniton_service.SpeechService;

/**
 * Created by bill on 11/20/17.
 */

@SuppressLint("Registered")
public class MainActivity extends PermissionActivity implements NavigationView.OnNavigationItemSelectedListener {

    // private ResponseReceiver receiver;
    private static final String TAG = "BtroadCast";
    private TextView response;
    private ToggleButton btnIput;
    private ProgressBar progressBar;
    private ProgressBar WaitAction;
    private ToggleButton continous;
    private Toolbar toolbar;
    private ForeGroundRecognition speechService;
    private Intent speechintent;
    private boolean mIsaved, assistantBound;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String result = intent.getStringExtra("result");

            if (!result.equals("")) {
                response.setText(result);
            } else {
                response.setText("");
            }



        }
    };
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui);
        if (Build.VERSION.SDK_INT < 23) Init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (speechService == null) {
            speechintent = new Intent(this, ForeGroundRecognition.class);
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
                if (mIsaved) {
                    mIsaved = false;
                    Toast.makeText(this, "Θα τερματίσει στη έξοδο", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Θα είναι πάντα ενεργό", Toast.LENGTH_LONG).show();

                    mIsaved = true;
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
        if (mIsaved) {
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
        setContinousRecognize();
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
        continous = findViewById(R.id.buttonContinous);
    }

    private void setProgress() {
        progressBar = findViewById(R.id.progressBar3);
        WaitAction = findViewById(R.id.progressBar4);
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

    private void clearProgressBar() {
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void clearWaitBar() {
        WaitAction.setIndeterminate(false);
        WaitAction.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showWaitBar() {
        WaitAction.setIndeterminate(true);
        WaitAction.setVisibility(View.VISIBLE);
    }

    private void startRecord(boolean b) {

        if (b) {
            speechService.StartInteract();
            showProgressBar();
        } else {
            clearProgressBar();
            speechService.StopSrecognition();
        }

    }

    private void setContinousRecognize() {
        continous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // recognition.setContinuousSpeechRecognition(false);
                } else {
                    //recognition.setContinuousSpeechRecognition(true);

                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(speechintent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(SpeechService.BroadcastAction));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 7) {
            Init();
        }
    }

}
