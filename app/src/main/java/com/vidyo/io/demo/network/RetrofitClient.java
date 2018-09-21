package com.vidyo.io.demo.network;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vidyo.io.demo.constants.Constants;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Summary : Class to create instance of retrofit
 * Description: Iniatialize the Retrofit class and make the singlton class
 * @author RSI
 * @date 16.08.2018
 */
public class RetrofitClient {
    public static String BASE_URL = Constants.baseUrl;
    private static Retrofit retrofit;
    private static HttpLoggingInterceptor mHttpLoggingInterceptor;

    private RetrofitClient() {
    }

    /**
     * Get retrofit instance
     */
    public static Retrofit getRetroInstance() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Get object of OkHTTPClient
     */
    private static OkHttpClient getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Log.i("Interceptor", interceptor.toString());
        // init cookie manager
        OkHttpClient okHttpClient;
        CookieHandler cookieHandler = new CookieManager();
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(interceptor)
                .cookieJar(new JavaNetCookieJar(cookieHandler))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(getHttpLoggingInterceptor())
                .build();
        return okHttpClient;
    }

    /**
     * Get object of HttpLoggingInterceptor
     */
    private static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        if (mHttpLoggingInterceptor == null) {
            mHttpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
//                    Timber.tag("OkHttp").d(message);
                    System.out.println("OkHttp " + message);
                }
            });
            // set your desired log level
            mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            // If Release then
//            mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return mHttpLoggingInterceptor;
    }
}
