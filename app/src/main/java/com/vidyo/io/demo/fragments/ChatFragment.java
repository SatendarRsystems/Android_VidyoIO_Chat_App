package com.vidyo.io.demo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Endpoint.ChatMessage;
import com.vidyo.io.demo.R;
import com.vidyo.io.demo.adapters.ChatMessagesAdapter;
import com.vidyo.io.demo.connector.ChatConnector;
import com.vidyo.io.demo.model.ChatMessageBean;
import com.vidyo.io.demo.utilities.NetworkUtils;
import java.util.ArrayList;

/**
 * Summary: Chat Fragment Component
 * Description: Show chat messages on chatting screen
 * @author RSI
 * @date 17.08.2018
 */
public class ChatFragment extends Fragment {

    /**
     * Declare view objects
     */
    private RecyclerView recyclerViewChat;
    private ImageButton imageButtonSendMessage;
    private EditText editTextMessage;

    /**
     * Declare chat message array list & adapter
     */
    private ArrayList<ChatMessageBean> chatMessageBeanArrayLists;
    private ChatMessagesAdapter chatMessagesAdapter;

    /**
     * Declare chat connector object
     */
    private ChatConnector chatConnector;
    private Connector vidyoConnector;

    /**
     * Get instance of ChatFragment
     * @param chatList Array list which contains initial list of chat messages
     */
    public static ChatFragment getInstance(ArrayList<ChatMessageBean> chatList) {
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("chat", chatList);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Constructor
     */
    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        typeCastViews(view);
        initializeMethodsAndVariables();
        setListenerOnViews();
        bindRecyclerView();
        return view;
    }

    /**
     * Typecast views to their respective objects
     */
    private void typeCastViews(View v) {
        recyclerViewChat = v.findViewById(R.id.recyclerViewChat);
        imageButtonSendMessage = v.findViewById(R.id.imageButtonSendMessage);
        editTextMessage = v.findViewById(R.id.editTextMessage);
    }

    /**
     * Initialize methods and variables
     */
    private void initializeMethodsAndVariables() {
        chatMessageBeanArrayLists = new ArrayList<>();
        chatMessageBeanArrayLists = (ArrayList<ChatMessageBean>) getArguments().getSerializable("chat");
        chatConnector = ChatConnector.getInstance(getActivity());
        if (chatConnector != null) {
            vidyoConnector = chatConnector.getVidyoConnector();
        }
    }

    /**
     * Set listener on views
     */
    private void setListenerOnViews() {
        imageButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            // Check if message is not empty
            if (!editTextMessage.getText().toString().isEmpty()) {

                // Check internet connection
                if (NetworkUtils.isConnected()) {

                    // Check if vidyoConnector object is not null
                    if (vidyoConnector != null) {

                        // Send message to vidyoConnector object
                        vidyoConnector.sendChatMessage(editTextMessage.getText().toString());

                        // Create an object of ChatMessage to display in chat list on UI
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.body=editTextMessage.getText().toString();
                        ChatMessageBean chatMessageBean= new ChatMessageBean();
                        chatMessageBean.setChatMessage(chatMessage);
                        chatMessageBean.setSelf(true);
                        chatMessageBeanArrayLists.add(chatMessageBean);

                        // Update chat list with updated array
                        updateList();

                        // Clean text from edittext
                        editTextMessage.setText("");
                    }
                }
                // Show error message if internet connection is not available
                else {
                    Toast.makeText(getActivity(), getString(R.string.error_no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
            // TODO remove this
            else {
                editTextMessage.setError(getString(R.string.label_chat_message));
            }
            }
        });
    }



    /**
     * Setup recycler view & bind it to adapter
     */
    private void bindRecyclerView() {
        chatMessagesAdapter = new ChatMessagesAdapter(getActivity(), chatMessageBeanArrayLists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerViewChat.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerViewChat.addItemDecoration(dividerItemDecoration);
        recyclerViewChat.setAdapter(chatMessagesAdapter);
    }

    /**
     * Update chat list array list
     * Called from activity
     */
    public void updateChatList(ArrayList<ChatMessageBean> chatArrayList) {
        chatMessageBeanArrayLists = chatArrayList;
        updateList();
    }

    /**
     * Update chat list adapter & scroll to bottom
     */
    private void updateList()
    {
        if (chatMessagesAdapter!=null)
        {
            chatMessagesAdapter.notifyDataSetChanged();
            if (chatMessageBeanArrayLists.size()>0)
            {
                recyclerViewChat.smoothScrollToPosition(chatMessageBeanArrayLists.size()-1);
            }
        }
    }

}
