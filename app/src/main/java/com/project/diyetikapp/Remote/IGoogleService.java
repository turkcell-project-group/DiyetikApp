package com.project.diyetikapp.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleService {

    @GET
    Call<String> getAdressName(@Url String url);
}
