package com.fci_zu_eng_gemy_95.foodsorders.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.fci_zu_eng_gemy_95.foodsorders.Model.Users;

public class Common {
    public static Users current_user;
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PASSWORD_KEY = "Password";

    public static String convertCodeToStatus(String code) {
        if (code.equals("0")) {
            return "Placed";
        } else if (code.equals("1")) {
            return "On My Way";
        } else
            return "Delivered";
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
