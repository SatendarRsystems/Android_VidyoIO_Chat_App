package com.vidyo.io.demo.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.io.demo.R;
import com.vidyo.io.demo.connector.ChatConnector;
import com.vidyo.io.demo.model.GetAccessTokenResponseBean;
import com.vidyo.io.demo.network.ApiRequestService;
import com.vidyo.io.demo.storage.SharedStorage;
import com.vidyo.io.demo.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * Summary: Login Component
 * Description: This is Login screen where user enter his username & meeting id to join chat
 * @author RSI
 * @date 16.08.2018
 */
public class LoginActivity extends BaseActivity implements Connector.IConnect {

    /**
     * Declare view objects
     */
    private Button buttonJoinMeeting;
    private EditText editTextUsername;
    private EditText editTextMeetingId;
    private CoordinatorLayout coordinatorLayout;
    private FrameLayout vidyoLayout;
    private final int PERMISSIONS_REQUEST_ALL = 1988;
    /**
     * Declare chat connector object
     */
    private ChatConnector chatConnector;

    /**
     * Declare api request service
     */
    private ApiRequestService apiRequestService;

    // Helps check whether app has permission to access what is declared in its manifest.
    // - Permissions from app's manifest that have a "protection level" of "dangerous".
    private static final String[] mPermissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        typeCastViews();
        setListenerOnViews();
        initializeMethodsAndVariables();

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

            }
        } else {

        }
    }

    /**
     * Typecast views to their respective objects
     */
    private void typeCastViews() {
        vidyoLayout = findViewById(R.id.vidyoLayout);
        buttonJoinMeeting = findViewById(R.id.buttonJoinMeeting);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextMeetingId = findViewById(R.id.editTextMeetingId);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
    }

    /**
     * Initializing methods & variables
     */
    private void initializeMethodsAndVariables() {
        apiRequestService = new ApiRequestService();
        chatConnector = ChatConnector.getInstance(this);
        chatConnector.setupConnection(this);
    }

    /**
     * Set listener on views
     */
    private void setListenerOnViews() {
        buttonJoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                if (NetworkUtils.isConnected()) {
                    String username = editTextUsername.getText().toString();
                    String meetingId = editTextMeetingId.getText().toString();
                    if (validateFields(username, meetingId)) {
                        callGetAccessToken(username, meetingId);
                        hideKeyboard();
                    }
                } else {
                    showSnakbar(getString(R.string.error_no_internet_connection), coordinatorLayout);
                }
            }
        });
    }

    /**
     * Method to validate fields
     * @param username  username for validation.
     * @param meetingId meetingId for validation.
     * @return A boolean value (true or false).
     */
    private boolean validateFields(String username, String meetingId) {
        boolean status = true;
        if (TextUtils.isEmpty(username)) {
            status = false;
            editTextUsername.setError(getString(R.string.error_username));
        }
        else if (username.contains(" "))
        {
            status = false;
            editTextUsername.setError(getString(R.string.error_username_space));
        }
        if (TextUtils.isEmpty(meetingId)) {
            status = false;
            editTextMeetingId.setError(getString(R.string.error_meeting_id));
        }
        return status;
    }

    /**
     * Method to call getAccessApi
     * @param username  userName for request query parameter.
     * @param meetingId meetingId used for join a meeting room.
     */
    private void callGetAccessToken(String username, final String meetingId) {
        showProgressDialog();
        apiRequestService.getAccessToken(username, new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object value) {

                // Check if value is not null
                if (value != null) {

                    // Check if value is an instance of GetAccessTokenResponseBean object
                    if (value instanceof GetAccessTokenResponseBean) {

                        // Convert response to GetAccessTokenResponseBean object
                        GetAccessTokenResponseBean getAccessTokenResponseBean = (GetAccessTokenResponseBean) value;

                        // Check if response status is success
                        if (getAccessTokenResponseBean.getStatus().equalsIgnoreCase("Success")) {

                            // Set meeting id to GetAccessTokenResponseBean object
                            getAccessTokenResponseBean.setMeetingId(meetingId);

                            // Save username & meeting id in shared storage
                            SharedStorage.setUsername(LoginActivity.this, getAccessTokenResponseBean.getUsername());
                            SharedStorage.setMeetingId(LoginActivity.this, meetingId);
                            SharedStorage.setAccessToken(LoginActivity.this, getAccessTokenResponseBean.getAccessToken());
                            // Start connection to vidyo.io server
                            connectToVidyo(getAccessTokenResponseBean, meetingId);
                        }
                        // If status is not success then dismiss dialog & show error message
                        else {
                            dissmissProgressDialog();
                            showSnakbar(getAccessTokenResponseBean.getStatus(), coordinatorLayout);
                        }
                    }
                    // If invalid format then dismiss dialog & show error message
                    else{
                        dissmissProgressDialog();
                        showSnakbar(getResources().getString(R.string.error_invalid_token_format), coordinatorLayout);
                    }
                }
                // If value is null dismiss dialog & show error message
                else
                {
                    dissmissProgressDialog();
                    showSnakbar(getResources().getString(R.string.error_no_token), coordinatorLayout);
                }
            }

            @Override
            public void onError(Throwable e) {
                dissmissProgressDialog();
                showSnakbar(e.getMessage(), coordinatorLayout);
            }


            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * Initiate connection to vidyo.io server when token acquired
     * @param getAccessTokenResponseBean  getAccessTokenResponseBean contains data of user.
     * @param meetingId meetingId used for join a meeting room.
     */
    public void connectToVidyo(GetAccessTokenResponseBean getAccessTokenResponseBean, String meetingId) {
        chatConnector.startConnection(this, vidyoLayout, getAccessTokenResponseBean, meetingId);
    }

    /**
     * Vidyo SDK's IConnect callback
     * Called when connection to vidyo.io server is successful
     */
    @Override
    public void onSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dissmissProgressDialog();
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            }
        });
    }

    /**
     * Vidyo SDK's IConnect callback
     * Called when vidyo.io server returned a failed response
     */
    @Override
    public void onFailure(final Connector.ConnectorFailReason connectorFailReason) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dissmissProgressDialog();
                Toast.makeText(LoginActivity.this, connectorFailReason + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Vidyo SDK's IConnect callback
     * Called when vidyo.io server disconnected
     */
    @Override
    public void onDisconnected(Connector.ConnectorDisconnectReason connectorDisconnectReason) {
    }
}
