package com.vidyo.io.demo.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.vidyo.io.demo.R;
import com.vidyo.io.demo.model.ChatMessageBean;
import com.vidyo.io.demo.utilities.NameBadgeUtil;
import java.util.ArrayList;

/**
 * Summary: Adapter Component
 * Description: Adapter to bind chat messages to recyclerview
 * @author RSI
 * @date 20.08.2018
 */
public class ChatMessagesAdapter extends RecyclerView.Adapter {

    /**
     * Declare Activity object
     */
    private Activity mActivity;

    /**
     * Declare ChatMessageBean object & array list
     */
    private ArrayList<ChatMessageBean> chatMessageBeanArrayList;
    private ChatMessageBean chatMessageBean;

    /**
     * Declare NameBadgeUtil object
     */
    private NameBadgeUtil nameBadgeUtil;

    /**
     * Constructor
     * @param activity Activity object to execute context methods
     * @param list  List of chat messages
     */
    public ChatMessagesAdapter(Activity activity, ArrayList<ChatMessageBean> list) {
        this.mActivity = activity;
        this.chatMessageBeanArrayList = list;
        this.nameBadgeUtil = new NameBadgeUtil(mActivity);
    }

    /**
     * Get view type of object
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return (chatMessageBeanArrayList.get(position).isSelf() ? ChatMessageBean.TYPE_SELF_MESSAGE : ChatMessageBean.TYPE_OTHER_MESSAGE);
    }

    /**
     * Create view holder object as per view type
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view;
        switch (viewType) {
            case ChatMessageBean.TYPE_SELF_MESSAGE:
                view = mActivity.getLayoutInflater().inflate(R.layout.item_chat_self_message, parent, false);
                return new SelfMessageHolder(view);
            case ChatMessageBean.TYPE_OTHER_MESSAGE:
                view = mActivity.getLayoutInflater().inflate(R.layout.item_chat_others_message, parent, false);
                return new OthersMessageHolder(view);
        }
      return null;
    }

    /**
     * Bind view holder's with chat message object as per their view type
     * @param holder ViewHolder of item
     * @param position Position of item
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        chatMessageBean = chatMessageBeanArrayList.get(position);
        if (getItemViewType(position) == ChatMessageBean.TYPE_SELF_MESSAGE) {
            SelfMessageHolder selfMessageHolder = (SelfMessageHolder) holder;
            if (chatMessageBean.getChatMessage().body!=null)
            {
                selfMessageHolder.textViewMessage.setText(chatMessageBean.getChatMessage().body);
            }

        } else if (getItemViewType(position) == ChatMessageBean.TYPE_OTHER_MESSAGE) {
            OthersMessageHolder othersMessageHolder = (OthersMessageHolder) holder;
            if (chatMessageBean.getChatMessage().body!=null)
            {
                othersMessageHolder.textViewMessage.setText(chatMessageBean.getChatMessage().body);
            }

            if (chatMessageBean.getUsername()!=null)
            {
                othersMessageHolder.textViewSenderName.setText(chatMessageBean.getUsername());
                othersMessageHolder.imageViewChatMemberBadge.setImageBitmap(nameBadgeUtil.generateCircleBitmap(ContextCompat.getColor(mActivity, R.color.colorPrimaryDark), 42, chatMessageBean.getUsername().substring(0, 1).toUpperCase()));

            }
           }
    }

    /**
     * Get item count if array list is not null
     */
    @Override
    public int getItemCount() {
        return (chatMessageBeanArrayList != null ? chatMessageBeanArrayList.size() : 0);
    }

    /**
     * Self messages view holder
     */
    public class SelfMessageHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage;

        public SelfMessageHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
        }
    }

    /**
     * Other group members messages view holder
     */
    public class OthersMessageHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage;
        private ImageView imageViewChatMemberBadge;
        private TextView textViewSenderName;

        public OthersMessageHolder(View itemView) {
            super(itemView);
            textViewMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            imageViewChatMemberBadge = (ImageView) itemView.findViewById(R.id.imageViewChatMemberBadge);
            textViewSenderName = (TextView) itemView.findViewById(R.id.textViewSenderName);
        }
    }


}
