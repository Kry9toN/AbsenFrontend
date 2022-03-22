package krypton.absenmobile.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import krypton.absenmobile.R;
import krypton.absenmobile.api.model.UserDetails;
import krypton.absenmobile.api.service.Interface;
import krypton.absenmobile.storage.Preferences;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FunctionUtils {

    private static final String TIME_SERVER = "time-a.nist.gov";
    
    private Context context;
    private Interface mInterface;

    public FunctionUtils(Context context, Interface mInterface) {
        this.context = context;
        this.mInterface = mInterface;
    }

    // Load alert dialog only OK button
    public static void dialogOnlyOK(Context context, int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(id)
                .setNegativeButton(R.string.oke, (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    // Enter Intent/Activity
    public static void enterActivity(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    // Fetch time from internet
    public static String curentTime() {
        String hasil = null;
        try {
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
            TimeInfo timeInfo = timeClient.getTime(inetAddress);

            long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            Date time = new Date(returnTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            hasil = simpleDateFormat.format(time);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hasil;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double mapDistanceCalculated(double lat1, double lat2, double lon1,
                                    double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.round(Math.sqrt(distance));
    }

    public void getDetailUser(String token, String username) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), username);
        Call<UserDetails> call = mInterface.getData(token, body);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsGuru() || response.body().getIsDudi()) {
                        Preferences.setGuru(context, true);
                    }
                    Preferences.setAdmin(context, response.body().getIsSuperuser());
                    Preferences.setLatitude(context, response.body().getLatitude());
                    Preferences.setLongitude(context, response.body().getLongitude());
                    Preferences.setUsername(context, response.body().getUsername());
                    Preferences.setName(context, response.body().getName());
                } else {
                    FunctionUtils.dialogOnlyOK(context, R.string.error_server_side);
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                FunctionUtils.dialogOnlyOK(context, R.string.gangguan_jaringan);
            }
        });
    }
}
