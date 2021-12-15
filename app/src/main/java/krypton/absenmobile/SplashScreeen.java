package krypton.absenmobile;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.scottyab.rootbeer.RootBeer;
import com.scottyab.rootbeer.util.Utils;

import krypton.absenmobile.guru.GuruMainActivity;
import krypton.absenmobile.siswa.SiswaMainActivity;
import krypton.absenmobile.storage.Preferences;
import krypton.absenmobile.util.security.IsolatedService;

public class SplashScreeen extends Activity {

    private static final String TAG = "SplashScreen";
    private IIsolatedService serviceBinder;
    private boolean bServiceBound;
    private boolean bIsMagisk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RootBeer rootBeer = new RootBeer(this);

        // Check use own native root check
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // When user manipulate Isolated service
                if(bServiceBound){
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
                            !Utils.isSelinuxFlagInEnabled() ||
                            rootBeer.checkForMagiskBinary()
                            ) {

                        //we found indication of root
                        Log.d(TAG, "Root Detedted");
                        dialogRootDetected();
                    } else {
                        // If not found Check again with Native check
                        try {
                            Log.d(TAG, "UID:"+ Os.getuid());
                            bIsMagisk = serviceBinder.isMagiskPresent();
                            if (bIsMagisk) {
                                dialogRootDetected();
                            } else {
                                enterLoginActivity();
                                getApplicationContext().unbindService(mIsolatedServiceConnection);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    Log.d(TAG, "Isolated can't bounding");
                    Toast.makeText(getApplicationContext(), "Terdeteksi terjadi kejanggalan pada aplikasi !!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }, 3000);
    }

    private void enterLoginActivity() {
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

    private void dialogRootDetected() {
        Toast.makeText(SplashScreeen.this, R.string.toast_root_detected, Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreeen.this);
        builder.setMessage(R.string.dialog_root_detected)
                .setNegativeButton(R.string.oke, (dialogInterface, i) -> finish()).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, IsolatedService.class);
        /*Binding to an isolated service */
        getApplicationContext().bindService(intent, mIsolatedServiceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mIsolatedServiceConnection = new ServiceConnection() {
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