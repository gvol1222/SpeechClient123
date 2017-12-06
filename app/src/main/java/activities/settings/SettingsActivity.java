package activities.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.widget.Switch;

import com.example.bill.Activities.R;

public class SettingsActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        if (savedInstanceState == null) {
            Fragment preferenceFragment = new SettingsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.pref_container, preferenceFragment);
            ft.commit();
            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        }
        Switch sw = findViewById(R.id.test);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = SP.edit();
        //edit.putBoolean("fw", sw.);
        edit.commit();

    }
}
