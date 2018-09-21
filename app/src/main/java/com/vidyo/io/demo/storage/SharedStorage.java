package com.vidyo.io.demo.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Summary : SharedStorage class to store the data in key value pair
 * Description: Used for SharedPreference to store username and meeting id
 * @author RSI
 * @date 17.08.2018
 */

public class SharedStorage {

    private static String VIDYO_KEY_DEMO = "VIDYO_DEMO";
    private static String KEY_USERNAME = "USERNAME";
    private static String KEY_MEETING_ID = "MEETING_ID";
    private static String KEY_ACCESS_TOKEN = "ACCESS_TOKEN";

    /**
     * Used to store username in shared storage
     */
    public static void setUsername(Context context, String username){
        SharedPreferences preferences = context.getSharedPreferences(VIDYO_KEY_DEMO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }

    /**
     * Used to store meeting id in shared storage
     */
    public static void setMeetingId(Context context, String meetingId){
        SharedPreferences preferences = context.getSharedPreferences(VIDYO_KEY_DEMO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_MEETING_ID, meetingId);
        editor.commit();
    }

    /**
     * Used to store access Token in shared storage
     */
    public static void setAccessToken(Context context, String accessToken){
        SharedPreferences preferences = context.getSharedPreferences(VIDYO_KEY_DEMO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.commit();
    }

    /**
     * Used to get username from shared storage
     */
    public static String getUsername(Context context){
        SharedPreferences preferences = context.getSharedPreferences(VIDYO_KEY_DEMO, Context.MODE_PRIVATE);
        return preferences.getString(KEY_USERNAME, null);
    }

    /**
     * Used to get meeting id from shared storage
     */
    public static String getMeetingId(Context context){
        SharedPreferences preferences = context.getSharedPreferences(VIDYO_KEY_DEMO, Context.MODE_PRIVATE);
        return preferences.getString(KEY_MEETING_ID, null);
    }

    /**
     * Used to get Access Token from shared storage
     */
    public static String getAccessToken(Context context){
        SharedPreferences preferences = context.getSharedPreferences(VIDYO_KEY_DEMO, Context.MODE_PRIVATE);
        return preferences.getString(KEY_ACCESS_TOKEN, null);
    }

    /**
     * Used to clear data from shared storage
     */
    public static void deletePreferences(Context context){
        SharedPreferences preferences = context.getSharedPreferences(VIDYO_KEY_DEMO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
