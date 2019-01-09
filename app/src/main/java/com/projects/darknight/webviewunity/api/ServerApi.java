package com.projects.darknight.webviewunity.api;

import com.projects.darknight.webviewunity.pojo.ServerPermission;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServerApi {
    @GET("api")
    Call<ServerPermission> allowGame();
}
