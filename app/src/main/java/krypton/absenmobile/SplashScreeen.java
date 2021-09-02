package krypton.absenmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import krypton.absenmobile.storage.Preferences;

public class SplashScreeen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO: menyambungkan antar aktifitas Siswa Guru atau Admin
                Intent login = new Intent(SplashScreeen.this, LoginActivity.class);
                if (!Preferences.getUserLogin(SplashScreeen.this)) {
                    startActivity(login);
                    finish();
                } else {
                    // TODO: menambah aktifitas jika user sudah pernah masuk
                }
            }
        }, 4000);
    }
}