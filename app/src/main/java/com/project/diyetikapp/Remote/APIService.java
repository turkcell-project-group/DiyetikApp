package com.project.diyetikapp.Remote;

import com.project.diyetikapp.Model.DataMessage;
import com.project.diyetikapp.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    //Firebase Token
                    "Authorization:key=AAAAQeaZuW0:APA91bESGQafTme0Jr6sP3F9in13OSFSOjS58Yj1tbUcw9XEUaNfnycjXdzTxvGtmgTarI-DfR2JZUUg1voR8kzIJKaEeCQLhZejYgE2M4EnfHhWuRotaFTfMeKKJN1-uUnGFZ0xx7sy"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
