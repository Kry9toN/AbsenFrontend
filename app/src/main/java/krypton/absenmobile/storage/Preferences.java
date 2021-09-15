package krypton.absenmobile.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    static final String TOKEN = "token";
    static final String USERNAME = "username";
    static final String GURU = "guru";
    static final String ADMIN = "admin";
    static final String IS_LOGIN = "is_login";
    static final String LONGITUDE = "longitude";
    static final String LATITUDE = "latitude";

    private static SharedPreferences getSharedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Token
    public static void setToken(Context context, String token) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(TOKEN, "Token " + token);
        editor.apply();
    }
    public static String getToken(Context context) {
        return getSharedPreference(context).getString(TOKEN, "");
    }

    // UserLogin = user already login or not
    public static void setUserLogin(Context context, boolean login) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(IS_LOGIN, login);
        editor.apply();
    }
    public static boolean getUserLogin(Context context) {
        return getSharedPreference(context).getBoolean(IS_LOGIN, false);
    }

    // Longitude
    public static void setLongitude(Context context, int longitude) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(LONGITUDE, longitude);
        editor.apply();
    }
    public static int getLongitude(Context context) {
        return getSharedPreference(context).getInt(LONGITUDE, 0);
    }

    // Latitude
    public static void setLatitude(Context context, int latitude) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(LATITUDE, latitude);
        editor.apply();
    }
    public static int getLatitude(Context context) {
        return getSharedPreference(context).getInt(LATITUDE, 0);
    }

    // Username
    public static void setUsername(Context context, String username) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(USERNAME, username);
        editor.apply();
    }
    public static String getUsername(Context context) {
        return getSharedPreference(context).getString(USERNAME, "");
    }

    // Guru
    public static void setGuru(Context context, Boolean isGuru) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(GURU, isGuru);
        editor.apply();
    }
    public static Boolean getGuru(Context context) {
        return getSharedPreference(context).getBoolean(GURU, false);
    }

    // Admin
    public static void setAdmin(Context context, Boolean isAdmin) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(ADMIN, isAdmin);
        editor.apply();
    }
    public static Boolean getAdmin(Context context) {
        return getSharedPreference(context).getBoolean(ADMIN, false);
    }
}
