package com.tejasdadhe.convokit.Modals;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Conversation {


    public static final int CONVERSATION_TYPE_PRIVATE = 1;
    public static final int CONVERSATION_TYPE_GROUP = 2;

    private String conversationTitle;
    private String lastConversationTime;
    private Message lastMessage;
    private String conversationPicUri;
    private boolean initialised;


    private String[] targetUserIds;
    private String firstUserId;
    private String extraData;

    private String conversationId;
    private int conversationType;


    @IntDef({CONVERSATION_TYPE_PRIVATE, CONVERSATION_TYPE_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConversationType {}



    public Conversation()
    {


    }

    public Conversation(String conversation_title, String last_conversation_date, Message last_message, String conversation_pic_uri)
    {
        this.conversationTitle = conversation_title;
        this.lastConversationTime = last_conversation_date;
        this.lastMessage = last_message;
        this.conversationPicUri = conversation_pic_uri;
    }




    public void setConversationType(@ConversationType int conversationType) {
        this.conversationType = conversationType;
    }

    @ConversationType
    public int getConversationType() {
        return conversationType;
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public void setConversationTitle(String conversationTitle) {
        this.conversationTitle = conversationTitle;
    }

    public String getLastConversationTime() {
        return lastConversationTime;
    }

    public void setLastConversationTime(String lastConversationTime) {
        this.lastConversationTime = lastConversationTime;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getConversationPicUri() {
        return conversationPicUri;
    }

    public void setConversationPicUri(String conversationPicUri) {
        this.conversationPicUri = conversationPicUri;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public boolean isInitialised() {
        return initialised;
    }


    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }


    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public String[] getTargetUserIds() {
        return targetUserIds;
    }

    public void setTargetUserIds(String[] targetUserIds) {
        this.targetUserIds = targetUserIds;
    }

    public String getFirstUserId() {
        return firstUserId;
    }

    public void setFirstUserId(String firstUserId) {
        this.firstUserId = firstUserId;
    }
}
