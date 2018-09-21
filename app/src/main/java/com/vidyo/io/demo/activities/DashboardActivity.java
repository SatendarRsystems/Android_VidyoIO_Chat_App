package com.vidyo.io.demo.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Endpoint.ChatMessage;
import com.vidyo.VidyoClient.Endpoint.Participant;
import com.vidyo.io.demo.R;
import com.vidyo.io.demo.connector.ChatConnector;
import com.vidyo.io.demo.fragments.ChatFragment;
import com.vidyo.io.demo.fragments.ParticipantListFragment;
import com.vidyo.io.demo.model.ChatMessageBean;
import com.vidyo.io.demo.storage.SharedStorage;
import java.util.ArrayList;


/**
 * Summary: Dashboard Component
 * Description: Dashboard container for fragments
 * @author RSI
 * @date 16.08.2018
 */
public class DashboardActivity extends BaseActivity implements Connector.IRegisterParticipantEventListener, Connector.IRegisterMessageEventListener {

    /**
     * Declare toolbar controls
     */
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private static int VIDEO_CHAT_INTENT_CODE = 1001;
    /**
     * Declare & initialize fragment titles & toolbar icons
     */
    private String[] titles = new String[]{"Participants List", "Chat"};
    private int[] icons = new int[]{R.mipmap.ic_chat_white, R.mipmap.ic_users_white};

    /**
     * Declare & initialize default fragment index
     */
    private int fragmentIndex = 0;

    /**
     * Declare fragment object to handle current fragment
     */
    private Fragment activeFragment;


    /**
     * Declare chat connector objects
     */
    private ChatConnector chatConnector;
    private Connector vidyoConnector;

    /**
     * Declare array lists for participants & chat messages
     */
    private ArrayList<Participant> participantArrayList;
    private ArrayList<ChatMessageBean> chatArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        typeCastViews();
        initializeMethodsAndVariables();
        initializeToolbar();
        setListenerOnViews();
        showFragment();
    }

    /**
     * Typecast views to their respective objects
     */
    private void typeCastViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarTitle);
    }

    /**
     * Initializing methods & variables
     */
    private void initializeMethodsAndVariables() {
        participantArrayList = new ArrayList<>();
        chatArrayList = new ArrayList<>();
        chatConnector = ChatConnector.getInstance(this);
        vidyoConnector = chatConnector.getVidyoConnector();
        if (vidyoConnector != null) {
            vidyoConnector.registerParticipantEventListener(this);
            vidyoConnector.registerMessageEventListener(this);
        }
    }

    /**
     * Initialize toolbar controls
     */
    private void initializeToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.ic_chat_white);
        getSupportActionBar().setTitle(null);
        toolbarTitle.setText(titles[fragmentIndex]);
    }

    /**
     * Set listener on views
     */
    private void setListenerOnViews()  {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentIndex = (fragmentIndex == 0 ? 1 : 0);
                showFragment();
            }
        });
    }

    /**
     * Show fragment at the time of first loading & when user is switching between views
     */
    private void showFragment() {
        // Check fragment index to show respective fragment
        if (fragmentIndex == 0)
            activeFragment = ParticipantListFragment.getInstance(participantArrayList);
        else
            activeFragment = ChatFragment.getInstance(chatArrayList);
        replaceFragement(activeFragment);

        // Set title & navigation icon as per active fragment index
        toolbarTitle.setText(titles[fragmentIndex]);
        toolbar.setNavigationIcon(icons[fragmentIndex]);
    }

    /**
     * Set data on fragment
     */
    private void setDataToFragment() {
        if (fragmentIndex == 0) {
            ((ParticipantListFragment) activeFragment).updateParticipantList(participantArrayList);
        } else {
            ((ChatFragment) activeFragment).updateChatList(chatArrayList);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(VIDEO_CHAT_INTENT_CODE == resultCode) {
            fragmentIndex = 1;
            showFragment();
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
            case R.id.action_video_chat:
                joinVideoCall();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Logout from application
     */
    private void logout() {
        disconnectToVidyo();
        removeSession();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * Join the Video call
     */
    private void joinVideoCall() {
        startActivityForResult(new Intent(this, VideoChatActivity.class),VIDEO_CHAT_INTENT_CODE);
    }

    /**
     * Vidyo SDK's callback
     * Called when new chat message is received in current group
     */
    @Override
    public void onChatMessageReceived(final Participant participant, final ChatMessage chatMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatMessageBean chatMessageBean = new ChatMessageBean();
                chatMessageBean.setChatMessage(chatMessage);
                chatMessageBean.setUsername(participant.getName());
                chatMessageBean.setSelf(false);
                chatArrayList.add(chatMessageBean);
                setDataToFragment();
            }
        });

    }

    @Override
    public void onParticipantJoined(Participant participant) {

    }

    @Override
    public void onParticipantLeft(Participant participant) {

    }

    @Override
    public void onDynamicParticipantChanged(ArrayList<Participant> arrayList) {
        participantArrayList = arrayList;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setDataToFragment();
            }
        });
    }

    @Override
    public void onLoudestParticipantChanged(Participant participant, boolean b) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectToVidyo();
        removeSession();
    }

    /**
     * Remove session variables from shared storage
     */
    public void removeSession() {
        SharedStorage.deletePreferences(this);
    }


    /**
     * Disconnect from vidyo.io server
     */
    public void disconnectToVidyo() {
        if (vidyoConnector != null) {
            vidyoConnector.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
