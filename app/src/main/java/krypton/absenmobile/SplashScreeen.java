package krypton.absenmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import krypton.absenmobile.guru.GuruMainActivity;
import krypton.absenmobile.siswa.SiswaMainActivity;
import krypton.absenmobile.storage.Preferences;

public class SplashScreeen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Preferences.getUserLogin(SplashScreeen.this)) {
                    Intent login = new Intent(SplashScreeen.this, LoginActivity.class);
                    startActivity(login);
                    finish();
                } else {
                    if (Preferences.getGuru(SplashScreeen.this)) {
                        Intent guru = new Intent(SplashScreeen.this, GuruMainActivity.class);
                        startActivity(guru);
                        finish();
                    } else if (!Preferences.getGuru(SplashScreeen.this)) {
                        Intent siswa = new Intent(SplashScreeen.this, SiswaMainActivity.class);
                        startActivity(siswa);
                        finish();
                    }
                }
            }
        }, 4000);
    }
}