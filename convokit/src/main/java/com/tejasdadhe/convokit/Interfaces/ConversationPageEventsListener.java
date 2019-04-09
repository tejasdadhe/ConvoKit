package com.tejasdadhe.convokit.Interfaces;

import android.view.View;

import com.tejasdadhe.convokit.Modals.Message;

public interface ConversationPageEventsListener {
    void OnMessageClick(Message message, View messageView, int position);
    void OnMessageLongClick(Message message, View messageView, int position);

    void OnQuotedMessageClick(Message quotedMessage, Message parentMessage, View parentMessageView, View quotedMessageView, int parentMessagePosition);

    void OnMessageSelected(Message lastSelectedMessage, View messageView, int lastSelectedMessagePosition, Message[] selectedMessages);
    void OnMessageUnselected(Message lastUnselectedMessage, View messageView, int lastUnselectedMessagePosition, Message[] selectedMessages);
    void OnSelectedMessagesCleared(int count);




}
