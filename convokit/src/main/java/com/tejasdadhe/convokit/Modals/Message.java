package com.tejasdadhe.convokit.Modals;



import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Message {

    public static final int MESSAGE_TYPE_SENT_MESSAGE = 1;
    public static final int MESSAGE_TYPE_RECEIVED_MESSAGE = 2;

    public static final int MESSAGE_FORMAT_TEXT=5;
    public static final int MESSAGE_FORMAT_IMAGE=6;
    public static final int MESSAGE_FORMAT_VIDEO=7;
    public static final int MESSAGE_FORMAT_AUDIO=8;
    public static final int MESSAGE_FORMAT_FILE=9;


    public static final int MESSAGE_STATUS_PENDING=30;
    public static final int MESSAGE_STATUS_SENT=31;
    public static final int MESSAGE_STATUS_RECEIVED =32;
    public static final int MESSAGE_STATUS_READ=33;
    public static final int MESSAGE_STATUS_SENDING_FAILED=34;
    public static final int MESSAGE_STATUS_FILE_UPLOADING=35;
    public static final int MESSAGE_STATUS_FILE_UPLOADED=36;






    @IntDef({MESSAGE_FORMAT_TEXT, MESSAGE_FORMAT_IMAGE,MESSAGE_FORMAT_VIDEO,MESSAGE_FORMAT_AUDIO,MESSAGE_FORMAT_FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageFormat {}


    @IntDef({MESSAGE_STATUS_PENDING, MESSAGE_STATUS_SENT, MESSAGE_STATUS_RECEIVED,MESSAGE_STATUS_READ,MESSAGE_STATUS_SENDING_FAILED,MESSAGE_STATUS_FILE_UPLOADED,MESSAGE_STATUS_FILE_UPLOADING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageStatus {}

    @Nullable @IntDef({MESSAGE_TYPE_SENT_MESSAGE, MESSAGE_TYPE_RECEIVED_MESSAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MessageType {}

    private String messageID;
    private String messageText;



    @Exclude
    private String authorName;
    @Exclude
    private int messageType;
    private int messageStatus;
    private int messageFormat;
    private String messageTime;
    private String mediaUrl;
    private String senderId;


    private Message quotedMessage;



    public Message()
    {

    }

    public Message(String messageID, String senderId, @MessageType int messageType, String messageText, int messageFormat, String messageTime)
    {

        this.senderId      = senderId;
        this.messageID     = messageID;
        this.messageText   = messageText;
        this.messageType   = messageType;
        this.messageTime   = messageTime;
        this.messageFormat = messageFormat;

    }


    public @NonNull String getMessageID() {
        return messageID;
    }

    public Message setMessageID(@NonNull String messageID) {
        this.messageID = messageID;
        return this;
    }

    public String getMessageText() {
        return messageText;
    }

    public Message setMessageText(String messageText) {
        this.messageText = messageText;
        return this;
    }

    @Exclude
    @MessageType
    public int getMessageType() {
        return messageType;
    }

    @Exclude
    public Message setMessageType(@MessageType int messageType) {
        this.messageType = messageType;
        return this;
    }


    @MessageStatus
    public int getMessageStatus() {
        return messageStatus;
    }

    public Message setMessageStatus(@MessageStatus int messageStatus) {
        this.messageStatus = messageStatus;
        return this;
    }


    @MessageFormat
    public int getMessageFormat() {
        return messageFormat;
    }

    public Message setMessageFormat(@MessageFormat int messageFormat) {
        this.messageFormat = messageFormat;
        return this;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public Message setMessageTime(String time) {
        this.messageTime = time;
        return this;
    }


    public String getMediaUrl() {
        return mediaUrl;
    }

    public Message setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
        return this;
    }

    public Message getQuotedMessage() {
        return quotedMessage;
    }

    public void setQuotedMessage(Message quotedMessage)
    {
        this.quotedMessage = quotedMessage;
    }

    public void removeQuotedMessage()
    {
        this.quotedMessage = null;
    }

    public String getSenderId() {
        return senderId;
    }

    public Message setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    @Exclude
    public String getAuthorName() {
        return authorName;
    }

    @Exclude
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
