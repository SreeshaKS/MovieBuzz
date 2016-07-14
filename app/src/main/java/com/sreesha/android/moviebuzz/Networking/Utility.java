package com.sreesha.android.moviebuzz.Networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Sreesha on 20-03-2016.
 */
public class Utility {
    public static final String CONNECTIVITY_WIFI = "WIFI";
    public static final String CONNECTIVITY_MOBILE_DATA = "MOBILE_DATA";
    public static final String CONNECTIVITY_CONNECTING_CONNECTED = "CONNECTING_OR_CONNECTED";
    public static final String CONNECTIVITY_NOT_CONNECTED = "NOT_CONNECTED";

    public static String getConnectionTypeStatus(Context context) {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo(context);
        if (activeNetworkInfo != null) {
            if (activeNetworkInfo.isConnectedOrConnecting()) {
                return CONNECTIVITY_CONNECTING_CONNECTED;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return CONNECTIVITY_WIFI;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return CONNECTIVITY_MOBILE_DATA;
            }
        } else {
            return CONNECTIVITY_NOT_CONNECTED;
        }
        return CONNECTIVITY_NOT_CONNECTED;
    }

    public static boolean isConnectedToNetwork(Context context) {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo(context);
        return activeNetworkInfo != null
                &&
                activeNetworkInfo.isConnectedOrConnecting();
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        return (
                (
                        (ConnectivityManager) context
                                .getSystemService(Context.CONNECTIVITY_SERVICE)
                )
                        .getActiveNetworkInfo()
        );
    }
}
