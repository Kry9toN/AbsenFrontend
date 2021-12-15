package krypton.absenmobile.siswa.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import krypton.absenmobile.R;
import krypton.absenmobile.api.Client;
import krypton.absenmobile.api.model.UserDetails;
import krypton.absenmobile.api.service.Interface;
import krypton.absenmobile.databinding.FragmentDashboardSiswaBinding;
import krypton.absenmobile.storage.Preferences;
import krypton.absenmobile.util.calculated.MapDistance;
import krypton.absenmobile.util.loading.LoadingAnimation;
import krypton.absenmobile.util.security.Permission;
import krypton.absenmobile.util.time.TimeCheck;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardSiswa extends Fragment implements OnMapReadyCallback {

    private static String TAG = "SiswaActivity";

    public double Latitude;
    public double Longitude;

    private FragmentDashboardSiswaBinding binding;
    private MapView mapView;
    private Interface mInterface;
    private LocationRequest locationRequest;
    private View root;
    private TextView textViewJamDatang;
    private TextView textViewJamPulang;
    private TextView textViewNama;
    private FirebaseRemoteConfig remoteConfig;
    private LoadingAnimation loadingAnimation;
    private Button btn1;
    private Button btn2;

    private static final String JAM_DATANG_MULAI = "jam_datang_mulai";
    private static final String JAM_DATANG_AKHIR = "jam_datang_akhir";
    private static final String JAM_PULANG_MULAI = "jam_pulang_mulai";
    private static final String JAM_PULANG_AKHIR = "jam_pulang_akhir";

    private String jam_datang_mulai;
    private String jam_datang_akhir;
    private String jam_pulang_mulai;
    private String jam_pulang_akhir;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardSiswaBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        Permission.checkLocation(getContext());
        Permission.checkPhoneState(getContext());

        mInterface = Client.getClient().create(Interface.class);

        // Fetch Config time
        remoteConfig = getFirebaseRemoteConfig();

        // Early fetch data every dashboard loaded
        getDetailUser(Preferences.getToken(getContext()), Preferences.getUsername(getContext()));

        // Fecth Data Map
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500);
        updateLocation();

        // Map view
        mapView = root.findViewById(R.id.map_siswa);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        // Jam
        textViewJamDatang = root.findViewById(R.id.jam_datang);
        textViewJamPulang = root.findViewById(R.id.jam_pulang);

        // Set jam
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Boolean>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        textViewJamDatang.setText(remoteConfig.getString(JAM_DATANG_MULAI) +
                                " - " + remoteConfig.getString(JAM_DATANG_AKHIR));
                        textViewJamPulang.setText(remoteConfig.getString(JAM_PULANG_MULAI) +
                                " - " + remoteConfig.getString(JAM_PULANG_AKHIR));

                        // Set variable
                        jam_datang_mulai = remoteConfig.getString(JAM_DATANG_MULAI);
                        jam_datang_akhir = remoteConfig.getString(JAM_DATANG_AKHIR);
                        jam_pulang_mulai = remoteConfig.getString(JAM_PULANG_MULAI);
                        jam_pulang_akhir = remoteConfig.getString(JAM_PULANG_AKHIR);

                    }
                });

        // Set nama
        textViewNama = root.findViewById(R.id.text_nama);
        textViewNama.setText(Preferences.getName(getContext()));

        btn1 = root.findViewById(R.id.datang);
        btn1.setOnClickListener(v -> {
            // Sync location
            updateLocation();
            // Take now time
            String nowTime = TimeCheck.time();

            Log.d(TAG, nowTime);

            // Calculated distance
            double hasil = MapDistance.Calculated(
                    Preferences.getLatitude(getContext()),
                    Preferences.getCurentLatitude(getContext()),
                    Preferences.getLongitude(getContext()),
                    Preferences.getCurentLongitude(getContext()));

            // if the distance exceeds 501.0, 501.0 is offset distance
            if (hasil <= 501.0) {
                if (timeAfterBeforeCheck(jam_datang_mulai, jam_datang_akhir, nowTime)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.berhasil_absen)
                            .setNegativeButton(R.string.oke, (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    btn1.setCompoundDrawables(null, null, null, null);
                }
            }
            if (hasil >= 501.0) {
                if (!timeAfterBeforeCheck(jam_datang_mulai, jam_datang_akhir, nowTime)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.jam_saat_absen)
                            .setNegativeButton(R.string.oke, (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    btn1.setCompoundDrawables(null, null, null, null);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.jarak_saat_absen)
                            .setNegativeButton(R.string.oke, (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    btn1.setCompoundDrawables(null, null, null, null);
                }
            }
        });

        btn2 = root.findViewById(R.id.pulang);
        btn2.setOnClickListener(v -> {
            // Sync location
            updateLocation();
            // Take now time
            String nowTime = TimeCheck.time();

            Log.d(TAG, nowTime);

            // Calculated distance
            double hasil = MapDistance.Calculated(
                    Preferences.getLatitude(getContext()),
                    Preferences.getCurentLatitude(getContext()),
                    Preferences.getLongitude(getContext()),
                    Preferences.getCurentLongitude(getContext()));

            // if the distance exceeds 501.0, 501.0 is offset distance
            if (hasil <= 501.0) {
                if (timeAfterBeforeCheck(jam_pulang_mulai, jam_pulang_akhir, String.valueOf(nowTime))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.berhasil_absen)
                            .setNegativeButton(R.string.oke, (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
            if (hasil >= 501.0) {
                if (!timeAfterBeforeCheck(jam_pulang_mulai, jam_pulang_akhir, String.valueOf(nowTime))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.jam_saat_absen)
                            .setNegativeButton(R.string.oke, (dialogInterface, i) -> dialogInterface.dismiss()).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.jarak_saat_absen)
                            .setNegativeButton(R.string.oke, (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }
        });
        return root;
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.getFusedLocationProviderClient(getContext())
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);

                            if (locationResult.getLocations().size() > 0) {
                                int index = locationResult.getLocations().size() - 1;
                                Latitude = locationResult.getLocations().get(index).getLatitude();
                                Longitude = locationResult.getLocations().get(index).getLongitude();

                                // Convert
                                float curentLatitude = (float)Latitude;
                                float curentLongitude = (float)Longitude;

                                Preferences.setCurentLatitude(getContext(), curentLatitude);
                                Preferences.setCurentLongitude(getContext(), curentLongitude);
                            }
                        }
                    }, Looper.getMainLooper());
        }
    }

    private void getDetailUser(String token, String username) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), username);
        Call<UserDetails> call = mInterface.getData(token, body);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {
                    Preferences.setLatitude(getContext(), response.body().getLatitude());
                    Preferences.setLongitude(getContext(), response.body().getLongitude());
                    Preferences.setName(getContext(), response.body().getName());
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.error_server_side)
                            .setNegativeButton(R.string.oke, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {

            }
        });
    }

    private boolean timeAfterBeforeCheck(String jamAwal, String jamAkhir, String time_curent) {
        try {
            // Jam awal
            Date jam_awal = new SimpleDateFormat("HH:mm").parse(jamAwal);
            Calendar calendar_awal = Calendar.getInstance();
            calendar_awal.setTime(jam_awal);
            calendar_awal.add(Calendar.DATE, 1);

            // Jam akhir
            Date jam_akhir = new SimpleDateFormat("HH:mm").parse(jamAkhir);
            Calendar calendar_akhir = Calendar.getInstance();
            calendar_akhir.setTime(jam_akhir);
            calendar_akhir.add(Calendar.DATE, 1);

            // Curent Time
            Date jam_curent = new SimpleDateFormat("HH:mm").parse(time_curent);
            Calendar calendar_curent = Calendar.getInstance();
            calendar_curent.setTime(jam_curent);
            calendar_curent.add(Calendar.DATE, 1);

            Date curent = calendar_curent.getTime();
            if (curent.after(calendar_awal.getTime()) && curent.before(calendar_akhir.getTime())) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static FirebaseRemoteConfig getFirebaseRemoteConfig() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        return remoteConfig;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Preferences.getLatitude(getContext()), Preferences.getLongitude(getContext())))
                .title("Lokasi PKL"));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Permission.checkLocation(getContext());
        Permission.checkPhoneState(getContext());
    }
}
