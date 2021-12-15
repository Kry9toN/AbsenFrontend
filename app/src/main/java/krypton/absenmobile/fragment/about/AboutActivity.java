package krypton.absenmobile.fragment.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import krypton.absenmobile.R;

public class AboutActivity extends PreferenceFragmentCompat {

    private final String TAG = "About";

    private static final String VERSION_LABEL = "version_app";
    private Preference mVersionLabel;
    private String version;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_about, rootKey);

        takeVersionName();
        mVersionLabel = (Preference) findPreference(VERSION_LABEL);
        String versionDisplayed = "v" + version;
        mVersionLabel.setSummary(versionDisplayed);
    }

    public void takeVersionName() {
        try {
            PackageInfo mpInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            version = mpInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Error can't take version name");
            e.printStackTrace();
        }
    }
}
