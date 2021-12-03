package krypton.absenmobile.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    static final String TOKEN = "token";
    static final String USERNAME = "username";
    static final String NAME = "name";
    static final String GURU = "guru";
    static final String ADMIN = "admin";
    static final String IS_LOGIN = "is_login";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";
    static final String CURENT_LATITUDE = "curent_latitude";
    static final String CURENT_LONGITUDE = "curent_longitude";

    private static SharedPreferences getSharedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Token
    public static void setToken(Context context, String token) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(TOKEN, token);
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
    public static void setLongitude(Context context, Float longitude) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putFloat(LONGITUDE, longitude);
        editor.apply();
    }
    public static Float getLongitude(Context context) {
        return getSharedPreference(context).getFloat(LONGITUDE, 0);
    }

    // Latitude
    public static void setLatitude(Context context, Float latitude) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putFloat(LATITUDE, latitude);
        editor.apply();
    }
    public static Float getLatitude(Context context) {
        return getSharedPreference(context).getFloat(LATITUDE, 0);
    }

    // Curent Longitude
    public static void setCurentLongitude(Context context, Float curentLongitude) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putFloat(CURENT_LONGITUDE, curentLongitude);
        editor.apply();
    }
    public static Float getCurentLongitude(Context context) {
        return getSharedPreference(context).getFloat(CURENT_LONGITUDE, 0);
    }

    // Curent Latitude
    public static void setCurentLatitude(Context context, Float CurentLatitude) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putFloat(CURENT_LATITUDE, CurentLatitude);
        editor.apply();
    }
    public static Float getCurentLatitude(Context context) {
        return getSharedPreference(context).getFloat(CURENT_LATITUDE, 0);
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

    // Name
    public static void setName(Context context, String name) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(NAME, name);
        editor.apply();
    }
    public static String getName(Context context) {
        return getSharedPreference(context).getString(NAME, "");
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
