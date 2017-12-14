package activities.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.bill.Activities.R;

/**
 * Created by bill on 12/5/17.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.app_preferences);
    }


}
