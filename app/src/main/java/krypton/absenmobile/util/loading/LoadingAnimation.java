package krypton.absenmobile.util.loading;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import krypton.absenmobile.R;

public class LoadingAnimation {
    String TAG = "LoadongAnimation";

    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingAnimation(Activity myactivity) {
        activity = myactivity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomDialog);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_anim,null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
        Log.d(TAG, "Loading start");
    }

    public void dismisDialog() {
        alertDialog.dismiss();
        Log.d(TAG, "Loading stop");
    }
}
