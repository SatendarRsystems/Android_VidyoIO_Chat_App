package com.vidyo.io.demo.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.vidyo.io.demo.application.MainApplication;

 /**
 * Summary : Check the internet connection
 * Description : This class ensure the network connection and other network utilities
 * @author RSI
 * @date 20.08.2018
 */
public class NetworkUtils {

    /**
     * Check for internet connection
     */
    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) MainApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnected();
    }
}
