package krypton.absenmobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.scottyab.rootbeer.RootBeer;
import com.scottyab.rootbeer.util.Utils;

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
    private boolean bIsMagisk = false;
    private boolean detect = false;

    private IIsolatedService serviceBinder;
    private Interface mInterface;
    private FunctionUtils functionUtils;
    private RootBeer rootBeer;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        rootBeer = new RootBeer(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            setContentView(R.layout.splashscreen);
        }

        // Add a callback that's called when the splash screen is animating to
        // the app content.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(splashScreenView -> {
                final ObjectAnimator slideUp = ObjectAnimator.ofFloat(
                        splashScreenView,
                        View.TRANSLATION_Y,
                        0f,
                        -splashScreenView.getHeight()
                );
                slideUp.setInterpolator(new AnticipateInterpolator());
                slideUp.setDuration(200L);

                // Call SplashScreenView.remove at the end of your custom animation.
                slideUp.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        splashScreenView.remove();
                        enterLoginActivity();
                    }
                });
                if (!detect) slideUp.start();
            });
            new Handler().postDelayed(this::rootDetectection, 3000);
        }

        // Sync Data From Server
        mInterface = Client.getClient().create(Interface.class);
        functionUtils = new FunctionUtils(this, mInterface);
        functionUtils.getDetailUser(Preferences.getToken(this), Preferences.getUsername(this));
    }


    private void rootDetectection() {
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
                detect = true;
            } else {
                // If not found Check again with Native check
                try {
                    Log.d(TAG, "UID:"+ Os.getuid());
                    bIsMagisk = serviceBinder.isMagiskPresent();
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
        }else{
            Log.d(TAG, "Isolated can't bounding");
            Toast.makeText(getApplicationContext(), "Terdeteksi terjadi kejanggalan pada aplikasi !!", Toast.LENGTH_SHORT).show();
            finish();
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
        FunctionUtils.dialogOnlyOK(this, R.string.dialog_root_detected);
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