package krypton.absenmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import krypton.absenmobile.api.Client;
import krypton.absenmobile.api.model.LoginData;
import krypton.absenmobile.api.service.Interface;
import krypton.absenmobile.api.model.Login;
import krypton.absenmobile.storage.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import krypton.absenmobile.siswa.SiswaMainActivity;

public class LoginActivity extends Activity {

    Button btnLogin;
    EditText inputLogin, inputPass;
    String username, password;
    Interface mInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btn_login);
        inputLogin = (EditText) findViewById(R.id.username);
        inputPass = (EditText) findViewById(R.id.password);

        mInterface = Client.getClient().create(Interface.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = inputLogin.getText().toString();
                password = inputPass.getText().toString();

                login(username, password);
            }
        });
    }

    private void login(String username, String password) {
        Login login = new Login(username, password);
        Call<LoginData> call = mInterface.login(login);
        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                if (response.isSuccessful()) {
                    Preferences.setToken(LoginActivity.this, response.body().getToken());

                    // TODO: membuat aktifitas mengambil data status user
                    Intent siswa = new Intent(LoginActivity.this, SiswaMainActivity.class);
                    startActivity(siswa);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Password salah", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginData> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}