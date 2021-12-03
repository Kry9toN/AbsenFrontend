package krypton.absenmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.system.Os;
import android.util.Log;
import android.widget.Toast;

import com.scottyab.rootbeer.RootBeer;

import krypton.absenmobile.guru.GuruMainActivity;
import krypton.absenmobile.siswa.SiswaMainActivity;
import krypton.absenmobile.storage.Preferences;
import krypton.absenmobile.util.security.IsolatedService;
import krypton.absenmobile.util.security.RootCheck;

public class SplashScreeen extends Activity {

    private static final String TAG = "DetectMagisk";
    private  IIsolatedService serviceBinder;
    private boolean bServiceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        // Check is device rooted
        RootBeer rootBeer = new RootBeer(this);
        if (!rootBeer.isRooted()) {
            // Find binary su on device
            if (RootCheck.findBinary("su")) {
                //we not found indication of root
                Toast.makeText(this, R.string.toast_root_detected, Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_root_detected)
                        .setNegativeButton(R.string.oke, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }

            //we not found indication of root
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(bServiceBound){
                        boolean bIsMagisk = false;
                        try {
                            Log.d(TAG, "UID:"+ Os.getuid());
                            bIsMagisk = serviceBinder.isMagiskPresent();
                            if(bIsMagisk)
                                enterLoginActivity();
                            else
                                Toast.makeText(getApplicationContext(), R.string.toast_root_detected, Toast.LENGTH_LONG).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                builder.setMessage(R.string.dialog_root_detected)
                                        .setNegativeButton(R.string.oke, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        }).show();

                            //getApplicationContext().unbindService(mIsolatedServiceConnection);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Isolated Service not bound", Toast.LENGTH_SHORT).show();
                    }


                }
            }, 5000);

        } else {
            Toast.makeText(this, R.string.toast_root_detected, Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_root_detected)
                    .setNegativeButton(R.string.oke, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).show();
        }
    }

    public void enterLoginActivity() {
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