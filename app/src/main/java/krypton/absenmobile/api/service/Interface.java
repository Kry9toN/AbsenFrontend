package krypton.absenmobile.api.service;

import krypton.absenmobile.api.model.Login;
import krypton.absenmobile.api.model.LoginData;
import krypton.absenmobile.api.model.UserDetails;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Interface {
    @POST("/api/v1/login/")
    Call<LoginData> login(@Body Login login);

    @POST("/api/v1/userdetails/")
    Call<UserDetails> getData(@Header("Authorization") String authToken);
}
