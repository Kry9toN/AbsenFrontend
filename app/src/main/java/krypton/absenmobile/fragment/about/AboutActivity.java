package krypton.absenmobile.fragment.about;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import krypton.absenmobile.BuildConfig;
import krypton.absenmobile.R;

public class AboutActivity extends PreferenceFragmentCompat {

    private final String TAG = "About";

    private static final String VERSION_LABEL = "version_app";
    private Preference mVersionLabel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_about, rootKey);
        mVersionLabel = (Preference) findPreference(VERSION_LABEL);
        String versionDisplayed = "v" + BuildConfig.VERSION_NAME;
        mVersionLabel.setSummary(versionDisplayed);
    }
}
