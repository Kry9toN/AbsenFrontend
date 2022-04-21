package krypton.absenmobile;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.system.Os;
import android.util.Log;
import android.widget.Toast;

import androidx.core.splashscreen.SplashScreen;

import com.scottyab.rootbeer.RootBeer;

import krypton.absenmobile.api.Client;
import krypton.absenmobile.api.service.Interface;
import krypton.absenmobile.guru.GuruMainActivity;
import krypton.absenmobile.siswa.SiswaMainActivity;
import krypton.absenmobile.storage.Preferences;
import krypton.absenmobile.util.FunctionUtils;
import krypton.absenmobile.util.security.IsolatedService;

public class SplashScreeen extends Activity {

    private static final String TAG = "SplashScreen";
    private boolean bServiceBound;
    private boolean detect = false;

    private IIsolatedService serviceBinder;
    private RootBeer rootBeer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> true );

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        rootBeer = new RootBeer(this);

        // Sync Data From Server
        if (Preferences.getUserLogin(SplashScreeen.this)) {
            Interface mInterface = Client.getClient().create(Interface.class);
            FunctionUtils functionUtils = new FunctionUtils(this, mInterface);
            functionUtils.getDetailUser(Preferences.getToken(this), Preferences.getUsername(this));
        }

        new Handler().postDelayed(() -> {
            if(bServiceBound) {
                rootDetectection();
            }else{
                Log.d(TAG, "Isolated can't bounding");
                Toast.makeText(getApplicationContext(), "Terdeteksi terjadi kejanggalan pada aplikasi !!", Toast.LENGTH_SHORT).show();
                finish();
            }
            if (!detect) enterLoginActivity();
        }, 3000);
    }

    private void rootDetectection() {
        // RootBeer Check
        if (rootBeer.isRooted() ||
                rootBeer.checkForSuBinary() ||
                rootBeer.checkSuExists() ||
                rootBeer.detectRootManagementApps() ||
                rootBeer.detectPotentiallyDangerousApps() ||
                rootBeer.detectRootCloakingApps() ||
                rootBeer.detectTestKeys() ||
                rootBeer.checkForBusyBoxBinary() ||
                rootBeer.checkForRWPaths() ||
                rootBeer.checkForDangerousProps() ||
                rootBeer.checkForRootNative() ||
                rootBeer.checkForMagiskBinary()
        ) {
            //we found indication of root
            Log.d(TAG, "Root Detected");
            dialogRootDetected();
            detect = true;
        } else {
            // If not found Check again with Native check
            try {
                Log.d(TAG, "UID:"+ Os.getuid());
                boolean bIsMagisk = serviceBinder.isMagiskPresent();
                Log.i(TAG, String.valueOf(bIsMagisk));
                if (bIsMagisk) {
                    dialogRootDetected();
                    detect = true;
                } else {
                    getApplicationContext().unbindService(mIsolatedServiceConnection);
                    detect = false;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void enterLoginActivity() {
        if (!Preferences.getUserLogin(SplashScreeen.this)) {
            FunctionUtils.enterActivity(SplashScreeen.this, LoginActivity.class);
            finish();
        } else {
            if (Preferences.getGuru(SplashScreeen.this)) {
                FunctionUtils.enterActivity(SplashScreeen.this, GuruMainActivity.class);
                finish();
            } else if (!Preferences.getGuru(SplashScreeen.this)) {
                FunctionUtils.enterActivity(SplashScreeen.this, SiswaMainActivity.class);
                finish();
            }
        }
    }

    private void dialogRootDetected() {
        Toast.makeText(SplashScreeen.this, R.string.toast_root_detected, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, IsolatedService.class);
        /*Binding to an isolated service */
        getApplicationContext().bindService(intent, mIsolatedServiceConnection, BIND_AUTO_CREATE);
    }

    private final ServiceConnection mIsolatedServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            serviceBinder = IIsolatedService.Stub.asInterface(iBinder);
            bServiceBound = true;
            Log.d(TAG, "Service bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bServiceBound = false;
            Log.d(TAG, "Service Unbound");
        }
    };
}