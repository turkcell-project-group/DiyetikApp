package com.project.diyetikapp.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.project.diyetikapp.Model.User;
import com.project.diyetikapp.Remote.APIService;
import com.project.diyetikapp.Remote.IGoogleService;
import com.project.diyetikapp.Remote.RetrofitClient;

public class Common {
    public static User currentUser;

    public static String PHONE_TEXT= "userPhone";

    public static final String INTENT_FOOD_ID = "FoodId";

    private  static  final String BASE_URL = "https://fcm.googleapis.com/";

    private  static  final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public  static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public  static IGoogleService getGoogleMapAPI(){
        return RetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static final String DELETE= "Delete";
    public static final String USER_KEY= "User";
    public static final String PWD_KEY= "Password";
    public static final String convertCodeToStatus(String code){
        if(code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

    public static boolean isConnectedToInterner(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if(info!=null){
                for(int i=0; i<info.length;i++){
                    if(info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
