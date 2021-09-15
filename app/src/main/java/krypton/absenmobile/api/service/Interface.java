package krypton.absenmobile.api.service;

import krypton.absenmobile.api.model.Login;
import krypton.absenmobile.api.model.LoginData;
import krypton.absenmobile.api.model.UserDetails;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Interface {
    @POST("/api/v1/login/")
    Call<LoginData> login(@Body Login login);

    @Multipart
    @POST("/api/v1/userdetails/")
    Call<UserDetails> getData(@Header("Authorization") String authToken,
                              @Part("username") RequestBody username);
}
