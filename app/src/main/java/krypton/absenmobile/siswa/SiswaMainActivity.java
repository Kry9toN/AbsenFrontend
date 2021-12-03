package krypton.absenmobile.siswa;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import krypton.absenmobile.R;
import krypton.absenmobile.api.Client;
import krypton.absenmobile.api.model.UserDetails;
import krypton.absenmobile.api.service.Interface;
import krypton.absenmobile.databinding.ActivitySiswaBinding;
import krypton.absenmobile.storage.Preferences;
import krypton.absenmobile.util.MapDistance;
import krypton.absenmobile.util.Permission;
import krypton.absenmobile.util.background.TimeCheck;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SiswaMainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public double Latitude;
    public double Longitude;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivitySiswaBinding binding;
    private MapView mapView;
    private GoogleMap mGoogleMap;
    private Context context;
    private TextView textViewJam;
    private TextView textViewNama;
    private Interface mInterface;
    private LocationServices locationServices;
    private LocationRequest locationRequest;
    private Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInterface = Client.getClient().create(Interface.class);

        // Early fetch data every dashboard loaded
        getDetailUser(Preferences.getToken(this), Preferences.getUsername(this));

        // Fecth Data Map
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500);
        updateLocation();

        binding = ActivitySiswaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarSiswa.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_siswa);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Map view
        mapView = findViewById(R.id.map_siswa);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        // Jam
        textViewJam = findViewById(R.id.jam_dashboard);
        new TimeCheck(this, textViewJam).execute();

        textViewNama = findViewById(R.id.text_nama);
        textViewNama.setText(Preferences.getName(this));


        btn1 = findViewById(R.id.datang);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sync location
                updateLocation();

                // Calculated distance
                double hasil = MapDistance.Calculated(
                        Preferences.getLatitude(SiswaMainActivity.this),
                        Preferences.getCurentLatitude(SiswaMainActivity.this),
                        Preferences.getLongitude(SiswaMainActivity.this),
                        Preferences.getCurentLongitude(SiswaMainActivity.this));

                // if the distance exceeds 501.0, 501.0 is offset distance
                if (hasil <= 501.0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SiswaMainActivity.this);
                    builder.setMessage(R.string.berhasil_absen)
                            .setNegativeButton(R.string.oke, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SiswaMainActivity.this);
                    builder.setMessage(R.string.jarak_saat_absen)
                            .setNegativeButton(R.string.oke, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            }
        });
    }

    private void updateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);

                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                int index = locationResult.getLocations().size() - 1;
                                Latitude = locationResult.getLocations().get(index).getLatitude();
                                Longitude = locationResult.getLocations().get(index).getLongitude();

                                // Convert
                                float curentLatitude = (float)Latitude;
                                float curentLongitude = (float)Longitude;

                                Preferences.setCurentLatitude(SiswaMainActivity.this, curentLatitude);
                                Preferences.setCurentLongitude(SiswaMainActivity.this, curentLongitude);
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
                    Preferences.setLatitude(SiswaMainActivity.this, response.body().getLatitude());
                    Preferences.setLongitude(SiswaMainActivity.this, response.body().getLongitude());
                    Preferences.setName(SiswaMainActivity.this, response.body().getName());
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SiswaMainActivity.this);
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_siswa);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Permission.checkLocation(this);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Preferences.getLatitude(this), Preferences.getLongitude(this)))
                .title("Lokasi PKL"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}