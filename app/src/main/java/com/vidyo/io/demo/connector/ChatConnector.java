package com.vidyo.io.demo.connector;

import android.app.Activity;
import android.content.Context;
import android.widget.FrameLayout;
import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.io.demo.activities.LoginActivity;
import com.vidyo.io.demo.model.GetAccessTokenResponseBean;

/**
 * Summary: ChatConnector Component
 * Description:used for create a instance Connector
 * @author RSI
 * @date 17.08.2018
 */
public class ChatConnector {

    public static boolean mVidyoClientInitialized = false;
    public static ChatConnector chatConnector;
    public static Connector vidyoConnector = null;
    private ChatConnector(){}

    /**
     * Return instance of connector class
     */
    public static ChatConnector getInstance(Context context){
        if(chatConnector == null){
            synchronized (context){
                chatConnector = new ChatConnector();
            }
        }
        return chatConnector;
    }

    /**
     * Initialize connector package
     */
    public void setupConnection(Activity activity){
        ConnectorPkg.setApplicationUIContext(activity);
        mVidyoClientInitialized = ConnectorPkg.initialize();
    }

    /**
     * Start connection to vidyo.io server
     */
    public void startConnection(LoginActivity activity, FrameLayout vidyoLayout, GetAccessTokenResponseBean getAccessTokenResponseBean, String meetingId){
        vidyoConnector = new Connector(vidyoLayout, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 15, "warning info@VidyoClient info@VidyoConnector", "", 0);
        vidyoConnector.showViewAt(vidyoLayout, 0, 0, vidyoLayout.getWidth(), vidyoLayout.getHeight());
        vidyoConnector.connect("prod.vidyo.io", getAccessTokenResponseBean.getAccessToken(), getAccessTokenResponseBean.getUsername(), meetingId, activity);
    }

    /**
     * Get instance of VidyoConnector
     */
    public static Connector getVidyoConnector(){
        return vidyoConnector;
    }
}
