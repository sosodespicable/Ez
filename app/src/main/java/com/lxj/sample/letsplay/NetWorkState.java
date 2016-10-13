package com.lxj.sample.letsplay;

/**
 * Created by Administrator on 2016/8/6 0006.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by lizhaotailang on 2016/3/18.
 * judge the network state, whether the network is connected
 * 判断当前的网络状态，是否有网络连接
 * WiFi或者是移动数据
 * or is wifi or mobile data
 */

public class NetWorkState {
    //检查是否连接到网络
    public static boolean networkConnected(Context context){
        if (context != null){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null){
                return info.isAvailable();
            }
        }
        return false;
    }

    //检查wifi是否连接
    public static boolean wifiConnected(Context context){
        if (context != null){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null){
                if (info.getType() == ConnectivityManager.TYPE_WIFI){
                    return info.isAvailable();
                }
            }
        }
        return false;
    }

    //检查移动网络是否连接
    public static boolean mobileDataConnected(Context context){
        if (context != null){
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null){
                if (info.getType() == ConnectivityManager.TYPE_MOBILE){
                    return  info.isAvailable();
                }
            }
        }
        return false;
    }

}
