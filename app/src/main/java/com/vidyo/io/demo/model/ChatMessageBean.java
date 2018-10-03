package com.vidyo.io.demo.model;

import com.vidyo.VidyoClient.Endpoint.ChatMessage;

import java.io.Serializable;

/**
 * Summary: Used for set get of user message
 * Description: ChatMessageBean use for parsing the json api and getter setter
 * @author RSI
 * @date 20.08.2018
 */
public class ChatMessageBean implements Serializable{

    public static final int TYPE_SELF_MESSAGE = 0;
    public static final int TYPE_OTHER_MESSAGE = 1;
    private boolean isSelf;
    private String username;
    private transient ChatMessage chatMessage;

    public static int getTypeSelfMessage() {
        return TYPE_SELF_MESSAGE;
    }

    public static int getTypeOtherMessage() {
        return TYPE_OTHER_MESSAGE;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
