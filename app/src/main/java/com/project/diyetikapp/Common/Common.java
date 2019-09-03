package com.project.diyetikapp.Common;

import com.project.diyetikapp.Model.User;

public class Common {
    public static User currentUser;
    public static final String convertCodeToStatus(String code){
        if(code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }
}
