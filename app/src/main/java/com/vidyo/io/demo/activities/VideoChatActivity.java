package com.vidyo.io.demo.activities;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Device.Device;
import com.vidyo.VidyoClient.Device.LocalCamera;
import com.vidyo.VidyoClient.Endpoint.LogRecord;
import com.vidyo.VidyoClient.NetworkInterface;
import com.vidyo.io.demo.R;
import com.vidyo.io.demo.connector.ChatConnector;
import com.vidyo.io.demo.layout.IVideoFrameListener;
import com.vidyo.io.demo.layout.VideoFrameLayout;
import com.vidyo.io.demo.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Summary: Video Chat Component
 * Description: Show the Participants Video frame layout on Video meeting
 *
 * @author RSI
 * @date 15.09.2018
 */

public class VideoChatActivity extends Activity implements
        View.OnClickListener,
        Connector.IRegisterLogEventListener,
        Connector.IRegisterNetworkInterfaceEventListener,
        Connector.IRegisterLocalCameraEventListener,
        IVideoFrameListener {

    // Define the various states of this application.
    enum VidyoConnectorState {
        Connecting,
        Connected,
        Disconnected,
    }


    // Helps check whether app has permission to access what is declared in its manifest.
    // - Permissions from app's manifest that have a "protection level" of "dangerous".
    private static final String[] mPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    // - This arbitrary, app-internal constant represents a group of requested permissions.
    // - For simplicity, this app treats all desired permissions as part of a single group.
    private final int PERMISSIONS_REQUEST_ALL = 1988;
    private static int VIDEO_CHAT_INTENT_CODE = 1001;

    private VidyoConnectorState mVidyoConnectorState = VidyoConnectorState.Disconnected;
    private Logger mLogger = Logger.getInstance();
    private Connector mVidyoConnector = null;
    private LocalCamera mLastSelectedCamera = null;
    private ToggleButton mToggleConnectButton;
    private ToggleButton mMicrophonePrivacyButton;
    private ToggleButton mCameraPrivacyButton;
    private LinearLayout mToolbarLayout;
    private Toolbar toolbar;
    private VideoFrameLayout mVideoFrame;
    private boolean mHideConfig = false;
    private boolean mAutoJoin = false;
    private boolean mAllowReconnect = true;
    private boolean mCameraPrivacy = false;
    private boolean mMicrophonePrivacy = false;
    private boolean mEnableDebug = false;
    private String mReturnURL = null;
    private String mExperimentalOptions = null;
    private boolean mRefreshSettings = true;
    private boolean mDevicesSelected = true;
    private boolean mVidyoCloudJoin = false;
    private String mPortal;
    private String mRoomKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        // Initialize the member variables
        toolbar = findViewById(R.id.toolbar);
        mToolbarLayout = (LinearLayout) findViewById(R.id.toolbarLayout);
        mVideoFrame = (VideoFrameLayout) findViewById(R.id.videoFrame);
        mVideoFrame.Register(this);

        // Set the onClick listeners for the buttons
        mToggleConnectButton = (ToggleButton) findViewById(R.id.chat_switch);
        mToggleConnectButton.setOnClickListener(this);
        mMicrophonePrivacyButton = (ToggleButton) findViewById(R.id.microphone_privacy);
        mMicrophonePrivacyButton.setOnClickListener(this);
        mCameraPrivacyButton = (ToggleButton) findViewById(R.id.camera_privacy);
        mCameraPrivacyButton.setOnClickListener(this);

        ToggleButton button = (ToggleButton) findViewById(R.id.camera_switch);
        button.setOnClickListener(this);
        button.setOnClickListener(this);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back);

        if (ChatConnector.mVidyoClientInitialized) {
            // Construct Connector and register for events.
            try {
                mVidyoConnector = ChatConnector.getVidyoConnector();
                mVidyoConnector.assignViewToCompositeRenderer(mVideoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 15);

                // Register for local camera events
                if (!mVidyoConnector.registerLocalCameraEventListener(this)) {
                    mLogger.Log("registerLocalCameraEventListener failed");
                }
                // Register for network interface events
                if (!mVidyoConnector.registerNetworkInterfaceEventListener(this)) {
                    mLogger.Log("registerNetworkInterfaceEventListener failed");
                }
                // Register for log events
                if (!mVidyoConnector.registerLogEventListener(this, "info@VidyoClient info@VidyoConnector warning")) {
                    mLogger.Log("registerLogEventListener failed");
                }

                // Beginning in Android 6.0 (API level 23), users grant permissions to an app while
                // the app is running, not when they install the app. Check whether app has permission
                // to access what is declared in its manifest.
                if (Build.VERSION.SDK_INT > 22) {
                    List<String> permissionsNeeded = new ArrayList<>();
                    for (String permission : mPermissions) {
                        // Check if the permission has already been granted.
                        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                            permissionsNeeded.add(permission);
                    }
                    if (permissionsNeeded.size() > 0) {
                        // Request any permissions which have not been granted. The result will be called back in onRequestPermissionsResult.
                        ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), PERMISSIONS_REQUEST_ALL);
                    } else {
                        this.resizeVidyoRenderer();
                    }
                } else {
                    this.resizeVidyoRenderer();
                }
            } catch (Exception e) {
                mLogger.Log("Connector Construction failed");
            }
        }
        setListenerOnViews();
    }


    /**
     * Set listener on views
     */
    private void setListenerOnViews() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Set the refreshSettings flag so the app settings are refreshed in onStart
     * New intent was received so set it to use in onStart
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mRefreshSettings = true;
        setIntent(intent);
    }

    /**
     * Initialize or refresh the app settings.
     * When app is first launched, mRefreshSettings will always be true.
     * Each successive time that onStart is called, app is coming back to foreground so check if the
     * settings need to be refreshed again, as app may have been launched via URI.
     */
    @Override
    protected void onStart() {
        mLogger.Log("onStart");
        super.onStart();
        if (mRefreshSettings &&
                mVidyoConnectorState != VidyoConnectorState.Connected &&
                mVidyoConnectorState != VidyoConnectorState.Connecting) {

            Intent intent = getIntent();
            Uri uri = intent.getData();

            // Check if app was launched via URI
            if (uri != null) {
                String param = uri.getQueryParameter("host");
                mReturnURL = uri.getQueryParameter("returnURL");
                mHideConfig = uri.getBooleanQueryParameter("hideConfig", false);
                mAutoJoin = uri.getBooleanQueryParameter("autoJoin", false);
                mAllowReconnect = uri.getBooleanQueryParameter("allowReconnect", true);
                mCameraPrivacy = uri.getBooleanQueryParameter("cameraPrivacy", false);
                mMicrophonePrivacy = uri.getBooleanQueryParameter("microphonePrivacy", false);
                mEnableDebug = uri.getBooleanQueryParameter("enableDebug", false);
                mExperimentalOptions = uri.getQueryParameter("experimentalOptions");

                // Note: the following parameters are used to connect to VidyoCloud systems, not Vidyo.io.
                mVidyoCloudJoin = (uri.getHost() != null) && uri.getHost().equalsIgnoreCase("join");
                if (mVidyoCloudJoin) {
                    // Do not display the Vidyo.io form in VidyoCloud mode.
                    mHideConfig = true;

                    // Populate portal and roomKey
                    param = uri.getQueryParameter("portal");
                    mPortal = param != null ? param : "";
                    param = uri.getQueryParameter("roomKey");
                    mRoomKey = param != null ? param : "";
                }
            } else {
                // If this app was launched by a different app, then get any parameters; otherwise use default settings.
                mReturnURL = intent.hasExtra("returnURL") ? intent.getStringExtra("returnURL") : null;
                mHideConfig = intent.getBooleanExtra("hideConfig", false);
                mAutoJoin = intent.getBooleanExtra("autoJoin", false);
                mAllowReconnect = intent.getBooleanExtra("allowReconnect", true);
                mCameraPrivacy = intent.getBooleanExtra("cameraPrivacy", false);
                mMicrophonePrivacy = intent.getBooleanExtra("microphonePrivacy", false);
                mEnableDebug = intent.getBooleanExtra("enableDebug", false);
                mExperimentalOptions = intent.hasExtra("experimentalOptions") ? intent.getStringExtra("experimentalOptions") : null;
                mVidyoCloudJoin = false;
            }
            // Apply the app settings.
            this.applySettings();
        }
        mRefreshSettings = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVidyoConnector != null) {
            // Specify the width/height of the view to render to.
            mVidyoConnector.showViewAt(mVideoFrame, 0, 0, mVideoFrame.getWidth(), mVideoFrame.getHeight());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mVidyoConnector != null) {
            mVidyoConnector.setMode(Connector.ConnectorMode.VIDYO_CONNECTORMODE_Foreground);

            if (!mDevicesSelected) {
                // Devices have been released when backgrounding (in onStop). Re-select them.
                mDevicesSelected = true;

                // Select the previously selected local camera and default mic/speaker
                mVidyoConnector.selectLocalCamera(mLastSelectedCamera);
                mVidyoConnector.selectDefaultMicrophone();
                mVidyoConnector.selectDefaultSpeaker();

                // Reestablish camera and microphone privacy states
                mVidyoConnector.setCameraPrivacy(mCameraPrivacy);
                mVidyoConnector.setMicrophonePrivacy(mMicrophonePrivacy);
            }
        }
    }


    // The device interface orientation has changed
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Refresh the dimensions of the renderer when the UI is redrawn after the orientation change.
        // Need to wait, since width/height values of the view are not updated at this point.
        this.resizeVidyoRenderer();
    }

    /*
     * Private Utility Functions
     */

    // Callback containing the result of the permissions request. If permissions were not previously obtained,
    // wait until this is received until calling refreshVidyoRenderer where Connector is initially rendered.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // If the expected request code is received, begin rendering video.
        if (requestCode == PERMISSIONS_REQUEST_ALL) {
            for (int i = 0; i < permissions.length; ++i)
                resizeVidyoRenderer();
        } else {

        }
    }

    // Resize the rendering of the video stream.
    private void resizeVidyoRenderer() {
        // Wait until mVideoFrame is drawn until getting calling showViewAt to resize.
        ViewTreeObserver viewTreeObserver = mVideoFrame.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mVideoFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    // Specify the width/height of the view to render to.
                    mVidyoConnector.showViewAt(mVideoFrame, 0, 0, mVideoFrame.getWidth(), mVideoFrame.getHeight());
                }
            });
        }
    }

    // Apply some of the app settings
    private void applySettings() {
        if (mVidyoConnector != null) {
            // If enableDebug is configured then enable debugging
            if (mEnableDebug) {
                mVidyoConnector.enableDebug(7776, "warning info@VidyoClient info@VidyoConnector");
            } else {
                mVidyoConnector.disableDebug();
            }

            // If cameraPrivacy is configured then mute the camera
            mCameraPrivacyButton.setChecked(false); // reset state
            if (mCameraPrivacy) {
                mCameraPrivacyButton.performClick();
            }

            // If microphonePrivacy is configured then mute the microphone
            mMicrophonePrivacyButton.setChecked(false); // reset state
            if (mMicrophonePrivacy) {
                mMicrophonePrivacyButton.performClick();
            }

            // Set experimental options if any exist
            if (mExperimentalOptions != null) {
                ConnectorPkg.setExperimentalOptions(mExperimentalOptions);
            }

            // If configured to auto-join, then simulate a click of the toggle connect button
            if (mAutoJoin) {
                mToggleConnectButton.performClick();
            }
        }
    }


    /*
     * Button Event Callbacks
     */

    @Override
    public void onClick(View v) {
        if (mVidyoConnector != null) {
            switch (v.getId()) {
                case R.id.chat_switch:
                    Intent intent = new Intent();
                    setResult(VIDEO_CHAT_INTENT_CODE, intent);
                    finish();//finishing activity
                    break;

                case R.id.camera_switch:
                    // Cycle the camera.
                    mVidyoConnector.cycleCamera();
                    break;

                case R.id.camera_privacy:
                    // Toggle the camera privacy.
                    mCameraPrivacy = mCameraPrivacyButton.isChecked();
                    mVidyoConnector.setCameraPrivacy(mCameraPrivacy);
                    break;

                case R.id.microphone_privacy:
                    // Toggle the microphone privacy.
                    mMicrophonePrivacy = mMicrophonePrivacyButton.isChecked();
                    mVidyoConnector.setMicrophonePrivacy(mMicrophonePrivacy);
                    break;

                default:

                    break;
            }
        } else {

        }
    }


    // Toggle visibility of the toolbar
    @Override
    public void onVideoFrameClicked() {
        if (mVidyoConnectorState == VidyoConnectorState.Connected) {
            if (mToolbarLayout.getVisibility() == View.VISIBLE) {
                mToolbarLayout.setVisibility(View.INVISIBLE);
            } else {
                mToolbarLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /*
     *  Connector Events
     */


    // Handle local camera events.
    @Override
    public void onLocalCameraAdded(LocalCamera localCamera) {

    }

    @Override
    public void onLocalCameraRemoved(LocalCamera localCamera) {

    }

    @Override
    public void onLocalCameraSelected(LocalCamera localCamera) {
        // If a camera is selected, then update mLastSelectedCamera.
        if (localCamera != null) {
            mLastSelectedCamera = localCamera;
        }
    }

    @Override
    public void onLocalCameraStateUpdated(LocalCamera localCamera, Device.DeviceState state) {

    }

    // Handle a message being logged.
    @Override
    public void onLog(LogRecord logRecord) {
        // No need to log to console here, since that is implicitly done when calling registerLogEventListener.
    }

    @Override
    public void onNetworkInterfaceAdded(NetworkInterface vidyoNetworkInterface) {

    }

    @Override
    public void onNetworkInterfaceRemoved(NetworkInterface vidyoNetworkInterface) {

    }

    @Override
    public void onNetworkInterfaceSelected(NetworkInterface vidyoNetworkInterface, NetworkInterface.NetworkInterfaceTransportType vidyoNetworkInterfaceTransportType) {

    }

    @Override
    public void onNetworkInterfaceStateUpdated(NetworkInterface vidyoNetworkInterface, NetworkInterface.NetworkInterfaceState vidyoNetworkInterfaceState) {

    }
}

