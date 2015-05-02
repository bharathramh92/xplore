package com.example.bharathramh.xplore;


//import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.preference.PreferenceFragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setBackgroundColor(Color.WHITE);
        getView().setClickable(true);

        EditTextPreference et = (EditTextPreference) findPreference(getResources().getString(R.string.shared_pref_radius_key));
        /*et.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String s = preference.getExtras().getString(getResources().getString(R.string.shared_pref_radius_key));
                Log.d("settings", s);
                return false;
            }
        });*/
        et.getEditText().setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        try {
                            int input = Integer.parseInt(dest.toString() + source.toString());
                            if (input<50000 && input >0)
                                return null;
                        } catch (NumberFormatException nfe) {
                        }
                        return "";
                    }
                }
        });

    }

/*@Override
    void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean prefCheckBox = sharedPreferences.getBoolean("PREF_CHECKBOX", false);
        settingCheckBox.setText("CHECKBOX preference = " + prefCheckBox.toString());
    }*/

}