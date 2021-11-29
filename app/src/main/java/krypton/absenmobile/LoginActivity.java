package krypton.absenmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import krypton.absenmobile.api.Client;
import krypton.absenmobile.api.model.Login;
import krypton.absenmobile.api.model.LoginData;
import krypton.absenmobile.api.model.UserDetails;
import krypton.absenmobile.api.service.Interface;
import krypton.absenmobile.guru.GuruMainActivity;
import krypton.absenmobile.siswa.SiswaMainActivity;
import krypton.absenmobile.storage.Preferences;
import krypton.absenmobile.util.Permission;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {

    Button btnLogin;
    EditText inputLogin, inputPass;
    Interface mInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btn_login);
        inputLogin = (EditText) findViewById(R.id.username);
        inputPass = (EditText) findViewById(R.id.password);

        mInterface = Client.getClient().create(Interface.class);

        // First check permission
        Permission.checkAll(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, password;
                username = inputLogin.getText().toString();
                password = inputPass.getText().toString();

                login(username, password);
            }
        });
    }

    private void login(String username, String password) {

        Intent siswa = new Intent(LoginActivity.this, SiswaMainActivity.class);
        Preferences.setUserLogin(LoginActivity.this, true);
        startActivity(siswa);
        finish();

//        Login login = new Login(username, password);
//        Call<LoginData> call = mInterface.login(login);
//        call.enqueue(new Callback<LoginData>() {
//            @Override
//            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
//                if (response.isSuccessful()) {
//                    // Menyimpan semua data user
//                    final String token = "Token " + response.body().getToken();
//                    Preferences.setToken(LoginActivity.this, token);
//                    Preferences.setUsername(LoginActivity.this, username);
//                    getDetailUser(token, username);
//                } else {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                    builder.setMessage(R.string.pw_salah)
//                            .setNegativeButton(R.string.oke, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.dismiss();
//                                }
//                            }).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginData> call, Throwable t) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                builder.setMessage(R.string.no_inet)
//                        .setNegativeButton(R.string.oke, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        }).show();
//            }
//        });
    }

    private void getDetailUser(String token, String username) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), username);
        Call<UserDetails> call = mInterface.getData(token, body);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                if (response.isSuccessful()) {
                    if (response.body().getIsGuru() || response.body().getIsDudi()) {
                        Preferences.setGuru(LoginActivity.this, true);
                    }
                    Preferences.setAdmin(LoginActivity.this, response.body().getIsSuperuser());
                    Preferences.setLatitude(LoginActivity.this, response.body().getLatitude());
                    Preferences.setLongitude(LoginActivity.this, response.body().getLongitude());

                    if (Preferences.getGuru(LoginActivity.this)) {
                        Intent guru = new Intent(LoginActivity.this, GuruMainActivity.class);
                        Preferences.setUserLogin(LoginActivity.this, true);
                        startActivity(guru);
                        finish();
                    } else if (!Preferences.getGuru(LoginActivity.this)) {
                        Intent siswa = new Intent(LoginActivity.this, SiswaMainActivity.class);
                        Preferences.setUserLogin(LoginActivity.this, true);
                        startActivity(siswa);
                        finish();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
}
