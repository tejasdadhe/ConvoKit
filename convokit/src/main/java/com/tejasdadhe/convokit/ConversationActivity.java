package com.tejasdadhe.convokit;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.tejasdadhe.convokit.Adapters.MessageAdapter;
import com.tejasdadhe.convokit.Interfaces.ConversationPageEventsListener;
import com.tejasdadhe.convokit.Modals.Conversation;
import com.tejasdadhe.convokit.Modals.Message;
import com.tejasdadhe.convokit.Modals.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ConversationActivity extends AppCompatActivity  {


    String TAG = " Conversation Activity : ";

    ConvoKit convoKit ;

    RecyclerView conversationPage;
    MessageAdapter messageAdapter;

    RecyclerView.LayoutManager conversationPageLayoutManager;

    Conversation conversation;
    User firstUser;


    ArrayList<Message> messageList;

    ChatServiceProvider chatServiceProvider;

    String conversationId, userId;

    EditText textMessageInput;
    Button sendMessageButton;

    ImageButton replyButton,forwardButton,addAttachmentButton,addImageAttachment,addVideoAttachment,addAudioAttachment,addDocumentAttachment,addContactAttachment;

    Message quotedMessage = null;

    private HashMap<String,Integer> messageMap;  // The format is <Message ID, MessageList Index>
    private HashMap<String,User> userMap;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conversation);


        conversationId = getIntent().getStringExtra("conversation_id");

        chatServiceProvider = new ChatServiceProvider(conversationId);


        conversationPage = findViewById(R.id.conversation_page_container);
        sendMessageButton = findViewById(R.id.send_message_button);
        textMessageInput = findViewById(R.id.text_message);


        replyButton         = findViewById(R.id.reply_button);
        forwardButton       = findViewById(R.id.forward_button);
        addAttachmentButton = findViewById(R.id.add_attachment_button);


        addImageAttachment    = findViewById(R.id.image_attachment_button);
        addAudioAttachment    = findViewById(R.id.audio_attachment_button);
        addVideoAttachment    = findViewById(R.id.video_attachment_button);
        addDocumentAttachment = findViewById(R.id.document_attachment_button);
        addContactAttachment  = findViewById(R.id.contact_attachment_button);


        conversationPageLayoutManager = new LinearLayoutManager(this);
        conversationPage.setLayoutManager(conversationPageLayoutManager);
        messageList = new ArrayList<>();
        messageMap = new HashMap<>();
        messageAdapter = new MessageAdapter(messageList, this);
        conversationPage.setAdapter(messageAdapter);


        // TODO: 01/03/19  To be removed for production


        userMap = new HashMap<>();


        convoKit = new ConvoKit(ConversationActivity.this);

        convoKit.with("tejas").fetchConversationDetails(conversationId, new ConvoKit.ConvoKitTaskListener() {
            @Override
            public void OnSuccess(Object object) {
                Conversation conversation = (Conversation) object;
                Log.d(TAG," Conversation  : " + conversation.getConversationId());
                convoKit.fetchTargetUsers(conversation.getTargetUserIds(), new ChatServiceProvider.UserFetchListener() {
                    @Override
                    public void OnUserFetchCompleted(User[] users) {
                        for (User user : users)
                        {
                            Log.d(TAG," Target Users : " + user.getDisplayName());
                            userMap.put(user.getUserId(),user);
                        }
                    }

                    @Override
                    public void OnUserFetchFailed() {
                        Log.d(TAG,"User Fetch Failed");
                    }

                    @Override
                    public void OnUserChanged(User newUser) {

                    }
                });
            }

            @Override
            public void OnFailure() {

            }
        });




        User firstUser = new ConvoKit(this).getFirstUser();
        User nextUser = new ConvoKit(this).getNextUser();

        userMap.put(firstUser.getUserId(), firstUser);
        userMap.put(nextUser.getUserId(), nextUser);




        //todo till here


        messageAdapter.addConverwsationPageEventListener(new ConversationPageEventsListener() {
            @Override
            public void OnMessageClick(Message message, View messageView, int position) {

            }

            @Override
            public void OnMessageLongClick(Message message, View messageView, int position) {

            }

            @Override
            public void OnQuotedMessageClick(Message quotedMessage, Message parentMessage, View parentMessageView, View quotedMessageView, int parentMessagePosition) {
                if(messageAdapter.getSelectedMessageCount() == 0)
                {
                    String messageId = quotedMessage.getMessageID();
                    if(messageMap.containsKey(messageId))
                    {

                        int position = messageMap.get(messageId);
                        navigateToMessage(position);

                    }
                }
                else
                {
                    parentMessageView.performClick();
                }
            }

            @Override
            public void OnMessageSelected(Message lastSelectedMessage, View messageView, int lastSelectedMessagePosition, final Message[] selectedMessages) {
                Log.d(TAG,"Message Selected ");
                messageView.setBackgroundColor(getResources().getColor(R.color.CONVOKIT_SELECTED_MESSAGE_BACKGROUND));

                forwardButton.setVisibility(View.VISIBLE);
                if(selectedMessages.length == 1)
                {


                    replyButton.setVisibility(View.VISIBLE);
                    replyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Message selectedMessage = selectedMessages[0];


                            ImageButton cancelReplyMessageButton = findViewById(R.id.cancel_reply_message_button);



                            cancelReplyMessageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    quotedMessage = null;
                                    findViewById(R.id.selected_message_preview).setVisibility(View.GONE);
                                }
                            });

                            quotedMessage = selectedMessage;
                            quotedMessage.removeQuotedMessage(); // This is to remove long nesting of quoted messages in database

                            replyButton.setVisibility(View.GONE);
                            forwardButton.setVisibility(View.GONE);

                            messageAdapter.clearSelectedMessages();

                            findViewById(R.id.selected_message_preview).setVisibility(View.VISIBLE);

                            View selected_text_message_view = findViewById(R.id.selected_text_message);
                            View selected_audio_message_view = findViewById(R.id.selected_audio_message);
                            View selected_media_message_view = findViewById(R.id.selected_media_message);
                            View selected_file_message_view = findViewById(R.id.selected_attachment_message);



                            switch (selectedMessage.getMessageFormat())
                            {
                                case Message.MESSAGE_FORMAT_TEXT :
                                {
                                    setSelectedMessageView(View.VISIBLE, View.GONE, View.GONE, View.GONE);
                                    if(selectedMessage.getMessageText() != null)
                                    {
                                        TextView selectedMessageText = selected_text_message_view.findViewById(R.id.selected_message_text);
                                        selectedMessageText.setText(selectedMessage.getMessageText());
                                        if(selectedMessage.getAuthorName() != null)
                                        {
                                            ((TextView)findViewById(R.id.selected_message_author_name)).setText(selectedMessage.getAuthorName());
                                        }
                                    }
                                    break;
                                }
                                case Message.MESSAGE_FORMAT_AUDIO :
                                {
                                    setSelectedMessageView(View.GONE, View.VISIBLE, View.GONE, View.GONE);
                                    break;
                                }
                                case Message.MESSAGE_FORMAT_IMAGE :
                                {
                                    setSelectedMessageView(View.GONE, View.GONE, View.VISIBLE, View.GONE);
                                    if(selectedMessage.getMediaUrl() != null)
                                    {
                                        ImageView previewImage = findViewById(R.id.selected_image_preview);
                                        Glide.with(getBaseContext())
                                                .load(selectedMessage.getMediaUrl())
                                                .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(2)))
                                                .into(previewImage);
                                    }
                                    break;
                                }
                                case Message.MESSAGE_FORMAT_VIDEO :
                                {
                                    setSelectedMessageView(View.GONE, View.GONE, View.VISIBLE, View.GONE);
                                    break;
                                }
                                case Message.MESSAGE_FORMAT_FILE :
                                {
                                    setSelectedMessageView(View.GONE, View.GONE, View.GONE, View.VISIBLE);
                                    break;
                                }
                            }
                        }
                    });
                }
                else
                {
                    replyButton.setVisibility(View.GONE);
                }
            }


            private void setSelectedMessageView(int selectedTextMessageVisibility, int selectedAudioMessageVisibility, int selectedMediaMessageVisibility, int selectedAttachmentMessageVisibility)
            {

                findViewById(R.id.selected_text_message).setVisibility(selectedTextMessageVisibility);
                findViewById(R.id.selected_audio_message).setVisibility(selectedAudioMessageVisibility);
                findViewById(R.id.selected_media_message).setVisibility(selectedMediaMessageVisibility);
                findViewById(R.id.selected_attachment_message).setVisibility(selectedAttachmentMessageVisibility);
            }

            @Override
            public void OnMessageUnselected(Message lastUnselectedMessage, View messageView, int lastUnselectedMessagePosition, Message[] selectedMessages) {
                Log.d(TAG,"Message UnSelected ");
                messageView.setBackgroundColor(Color.parseColor("#00000000"));
                if(selectedMessages.length == 1)
                {
                    replyButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    replyButton.setVisibility(View.GONE);
                    if(selectedMessages.length == 0)
                        forwardButton.setVisibility(View.GONE);
                }

            }

            @Override
            public void OnSelectedMessagesCleared(int count) {
                int childrenCount = conversationPage.getChildCount();
                for(int i=0; i<childrenCount; i++)
                {
                    conversationPage.getChildAt(i).setBackgroundColor(Color.parseColor("#00000000"));
                }
            }
        });


        chatServiceProvider.fetchConversationData(conversationId,new ChatServiceProvider.ConversationFetchListener() {
            @Override
            public void OnSuccess(Conversation conversation) {

                setConversation(conversation);

                List<String> targetUserIds = conversation.getTargetUserIds();

                chatServiceProvider.fetchTargetUsers(targetUserIds, new ChatServiceProvider.UserFetchListener() {
                    @Override
                    public void OnUserFetchCompleted(User[] users) {
                        int size = users.length;
                        if(size > 0)
                        {
                            for (User user : users) {
                                userMap.put(user.getUserId(), user);
                                if (user.getUserId().equals(getConversation().getFirstUserId()))
                                    setFirstUser(user);
                            }
                            chatServiceProvider.fetchMessages(new ChatServiceProvider.MessageFetchingListener()
                            {
                                @Override
                                public void OnMessageFetchSuccessful(ArrayList<Message> arrayList) {

                                    for(Message message : arrayList)
                                    {
                                        if(message.getSenderId().equals(ConvoKit.getFirstUser().getUserId()))
                                        {
                                            message.setMessageType(Message.MESSAGE_TYPE_SENT_MESSAGE);
                                            message.setMessageStatus(Message.MESSAGE_STATUS_SENT);
                                        }
                                        else
                                        {
                                            message.setMessageType(Message.MESSAGE_TYPE_RECEIVED_MESSAGE);
                                        }

                                        if(userMap.containsKey(message.getSenderId()))
                                        {
                                            User author = userMap.get(message.getSenderId());
                                            if(author != null)
                                            {
                                                String authorName = author.getDisplayName();
                                                message.setAuthorName(authorName);
                                                Log.d(TAG," Sender Key :" + message.getSenderId());
                                                Log.d(TAG," Author Name :" + authorName);
                                            }

                                        }

                                        if(message.getQuotedMessage() != null)
                                        {
                                            if(userMap.containsKey(message.getQuotedMessage().getSenderId()))
                                            {
                                                User author = userMap.get(message.getQuotedMessage().getSenderId());
                                                if(author != null)
                                                {
                                                    String authorName = author.getDisplayName();
                                                    message.getQuotedMessage().setAuthorName(authorName);
                                                    Log.d(TAG," Quoted Msg Sender Key :" + message.getSenderId());
                                                    Log.d(TAG," Quoted Msg Author Name :" + authorName);
                                                }

                                            }
                                        }


                                        messageList.add(message);
                                        messageMap.put(message.getMessageID(),messageList.size()-1);
                                        messageAdapter.notifyItemInserted(messageList.size()-1);
                                        conversationPage.smoothScrollToPosition(messageList.size()-1);
                                    }

                                }

                                @Override
                                public void OnMesssageFetchFailure()
                                {
                                    conversationPage.setVisibility(View.GONE);
                                    findViewById(R.id.error_view).setVisibility(View.VISIBLE);
                                    Log.d(TAG," Fetch Failure ");
                                }
                            });
                        }
                    }

                    @Override
                    public void OnUserFetchFailed() {}

                    @Override
                    public void OnUserChanged(User newUser) {}
                });





            }

            @Override
            public void OnFailure() {

            }
        });



        chatServiceProvider.registerForMessageEvents(new ChatServiceProvider.MessageEventListener() {
            @Override
            public void OnNewMessageRecieved(Message recievedMessage) {
                Log.d(TAG, " New Message Recieved ");
            }

            @Override
            public void OnMessageDeleted() {

            }

            @Override
            public void OnMessageRead() {

            }

            @Override
            public void OnMessageDelivered() {

            }

            @Override
            public void OnListenerError(String error) {

            }
        });


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addSentMessage();
                //addRecievedMessage();

//                Message draftMessage = new Message();
//                if(quotedMessage != null)
//                {
//                    quotedMessage.removeQuotedMessage();
//                    draftMessage.setQuotedMessage(quotedMessage);
//                }
//
//
//                sendFileDummmy();
            }
        });

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyButton.setVisibility(View.GONE);
                forwardButton.setVisibility(View.GONE);
                messageAdapter.clearSelectedMessages();



            }
        });

        addAttachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.attachment_panel).getVisibility() == View.VISIBLE)
                    findViewById(R.id.attachment_panel).setVisibility(View.GONE);
                else
                    findViewById(R.id.attachment_panel).setVisibility(View.VISIBLE);


                addImageAttachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CustomPermissionManager(ConversationActivity.this).checkPermmission();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                });

                addAudioAttachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CustomPermissionManager(ConversationActivity.this).checkPermmission();
                        Intent intent = new Intent();
                        intent.setType("audio/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                });

                addVideoAttachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CustomPermissionManager(ConversationActivity.this).checkPermmission();
                        Intent intent = new Intent();
                        intent.setType("video/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                });

                addDocumentAttachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CustomPermissionManager(ConversationActivity.this).checkPermmission();
                        Intent intent = new Intent();
                        intent.setType("document/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                });

                addContactAttachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CustomPermissionManager(ConversationActivity.this).checkPermmission();
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
                    }
                });




            }
        });












//                Code for sending a text message
//                String message_string = textMessageInput.getText().toString();
//                if(!message_string.isEmpty())
//                {
//                    final Message newMessage = new Message()
//                            .setMessageType(Message.MESSAGE_TYPE_SENT_MESSAGE)
//                            .setMessageText(message_string)
//                            .setSenderId(userId)
//                            .setMessageFormat(Message.MESSAGE_FORMAT_TEXT)
//                            .setMessageStatus(Message.MESSAGE_STATUS_PENDING)
//                            .setMessageTime(((Long)(System.currentTimeMillis()/1000)).toString());
//
//                    Log.d(TAG,"Message added to send List");
//
//                    final int position = messageList.size();
//                    messageList.add(newMessage);
//                    messageAdapter.notifyItemInserted(position);
//                    conversationPage.smoothScrollToPosition(position);
//
//                    chatServiceProvider.sendMessage(newMessage, new ChatServiceProvider.MessageActionListener() {
//                        @Override
//                        public void OnMessageSendSuccess()
//                        {
//                            Log.d(TAG,"Message sent successfully");
//                            messageList.get(position).setMessageStatus(Message.MESSAGE_STATUS_SENT);
//                            messageAdapter.notifyItemChanged(position);
//                            conversationPage.smoothScrollToPosition(position);
//                        }
//                        @Override
//                        public void OnMessageSendFailure()
//                        {
//                            messageList.get(position).setMessageStatus(Message.MESSAGE_STATUS_SENDING_FAILED);
//                            messageAdapter.notifyItemChanged(position);
//                        }
//                    });
//                }

    }

    public void sendFileDummmy()
    {
        Message dummyMessage = new Message();
        dummyMessage.setSenderId("me");
        dummyMessage.setMessageStatus(Message.MESSAGE_STATUS_PENDING);
        dummyMessage.setMessageType(Message.MESSAGE_TYPE_SENT_MESSAGE);
        dummyMessage.setMessageText("Some dummy text...");
        dummyMessage.setMediaUrl("ddasdasd");
        dummyMessage.setMessageFormat(Message.MESSAGE_FORMAT_AUDIO);

        messageList.add(dummyMessage);
        messageMap.put(dummyMessage.getMessageID(),messageList.size()-1);
        messageAdapter.notifyItemInserted(messageList.size()-1);
        conversationPage.smoothScrollToPosition(messageList.size()-1);
    }

    private void navigateToMessage(final int messagePosition)
    {
        Log.d(TAG," position " + messagePosition);
        final View messageView = conversationPage.getLayoutManager().findViewByPosition(messagePosition);
        if(messageView == null)
        {
            conversationPage.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    recyclerView.removeOnScrollListener(this);
                    View newMessageView = conversationPage.getLayoutManager().findViewByPosition(messagePosition);
                    if(newMessageView != null)
                        highlightMessage(newMessageView);
                    else
                        navigateToMessage(messagePosition);
                }
            });
            conversationPage.smoothScrollToPosition(messagePosition);
        }
        else
            highlightMessage(messageView);
    }

    private void highlightMessage(@NonNull View messageView)
    {

        final Handler handler = new Handler();
        final View finalMessageView = messageView;
        (new Thread(){
            @Override
            public void run(){

                for(int i=255; i>=0 ; i--){
                    final int finalI = i;
                    handler.post(new Runnable(){
                        public void run(){
                            finalMessageView.setBackgroundColor(Color.argb(finalI, 255,0,0));
                        }
                    });
                    try { sleep(2); }
                    catch (InterruptedException e) { e.printStackTrace(); }

                }
            }
        }).start();
    }


    public Conversation getConversation() {
        return conversation;
    }

    private void setConversation(Conversation conversation)
    {
        this.conversation = conversation;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public void addSentMessage()
    {

        if(!textMessageInput.getText().toString().isEmpty())
        {
            String message = textMessageInput.getText().toString();
            textMessageInput.setText("");
            Message draftMessage = new Message();
            draftMessage.setAuthorName(ConvoKit.getFirstUser().getDisplayName());
            draftMessage.setSenderId(ConvoKit.getFirstUser().getUserId());
            draftMessage.setMessageText(message);
            draftMessage.setMessageFormat(Message.MESSAGE_FORMAT_TEXT);
            draftMessage.setMessageStatus(Message.MESSAGE_STATUS_PENDING);
            draftMessage.setMessageType(Message.MESSAGE_TYPE_SENT_MESSAGE);

            if(quotedMessage != null)
            {
                draftMessage.setQuotedMessage(quotedMessage);
                quotedMessage = null;
                findViewById(R.id.selected_message_preview).setVisibility(View.GONE);

            }



            final int position = messageList.size();
            messageList.add(draftMessage);
            messageAdapter.notifyItemInserted(position);
            conversationPage.smoothScrollToPosition(position);

            convoKit.with("tejas").sendMessage(draftMessage,conversationId, new ChatServiceProvider.MessageActionListener() {
                @Override
                public void OnMessageSendSuccess() {
                    messageList.get(position).setMessageStatus(Message.MESSAGE_STATUS_SENT);
                    messageAdapter.notifyItemChanged(position);
                }

                @Override
                public void OnMessageSendFailure() {

                }
            });
        }
    }

    public void addRecievedMessage()
    {
        if(!textMessageInput.getText().toString().isEmpty())
        {
            String message = textMessageInput.getText().toString();
            Message draftMessage = new Message();
            draftMessage.setAuthorName(ConvoKit.getNextUser().getDisplayName());
            draftMessage.setSenderId(ConvoKit.getNextUser().getUserId());
            Log.d(TAG,ConvoKit.getNextUser().getUserId());
            draftMessage.setMessageText(message);
            draftMessage.setMessageFormat(Message.MESSAGE_FORMAT_TEXT);
            chatServiceProvider.sendMessage(draftMessage, new ChatServiceProvider.MessageActionListener() {
                @Override
                public void OnMessageSendSuccess() {

                }

                @Override
                public void OnMessageSendFailure() {

                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        findViewById(R.id.attachment_panel).setVisibility(View.GONE);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Log.d(TAG,"Recieved a null file");
                //Display an error
                return;
            }
            Log.d(TAG,"Recieved a file");
            try {



                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                String fileSize = "1";
                String displayName = myFile.getName();

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            fileSize = cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE));

                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }

                String fileExt = MimeTypeMap.getFileExtensionFromUrl(myFile.toString());

                if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                    //If scheme is a content
                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    fileExt = mime.getExtensionFromMimeType(this.getContentResolver().getType(uri));
                } else {
                    //If scheme is a File
                    //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                    fileExt = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

                }

                Message imageMessage = new Message();
                imageMessage.setMessageFormat(Message.MESSAGE_FORMAT_IMAGE);
                imageMessage.setMessageStatus(Message.MESSAGE_STATUS_PENDING);
                imageMessage.setAuthorName(ConvoKit.getFirstUser().getDisplayName());
                imageMessage.setSenderId(ConvoKit.getFirstUser().getUserId());
                imageMessage.setMediaUrl(uriString);
                imageMessage.setMessageType(Message.MESSAGE_TYPE_SENT_MESSAGE);




                final int position = messageList.size();
                messageList.add(imageMessage);
                messageAdapter.notifyItemInserted(position);
                conversationPage.smoothScrollToPosition(position);



                chatServiceProvider.uploadFile(uri, new ChatServiceProvider.FileUploadListener() {
                    @Override
                    public void OnFileUploadProgress(double progress) {

                    }

                    @Override
                    public void OnFileUploadPaused() {

                    }

                    @Override
                    public void OnFileUploadSuccess(String downloadUri) {

                    }

                    @Override
                    public void OnFileUploadFaiure() {

                    }
                });





                chatServiceProvider.uploadFile(uri, new ChatServiceProvider.FileUploadListener() {
                    @Override
                    public void OnFileUploadProgress(double progress) {
                        Log.d(TAG,"File Upload Progress " + progress);
                    }

                    @Override
                    public void OnFileUploadPaused() {
                        Log.d(TAG,"File Upload Successful ");
                    }

                    @Override
                    public void OnFileUploadSuccess(String downloadUrl) {
                        Log.d(TAG,"File Upload Successful ");
                        Message draftMessage = messageList.get(position);
                        draftMessage.setMediaUrl(downloadUrl);
                        draftMessage.setMessageStatus(Message.MESSAGE_STATUS_FILE_UPLOADED);
                        if(quotedMessage != null)
                        {
                            draftMessage.setQuotedMessage(quotedMessage);
                            quotedMessage = null;
                        }

                        chatServiceProvider.sendMessage(draftMessage, new ChatServiceProvider.MessageActionListener() {
                            @Override
                            public void OnMessageSendSuccess() {
                                Log.d(TAG," Message Sent Sccessfully ");
                                messageList.get(position).setMessageStatus(Message.MESSAGE_STATUS_SENT);
                                messageAdapter.notifyItemChanged(position);
                            }

                            @Override
                            public void OnMessageSendFailure() {

                            }
                        });
                    }

                    @Override
                    public void OnFileUploadFaiure() {
                        Log.d(TAG,"File Upload Failure ");
                    }
                });



                Log.d(TAG,"File URI " + uriString);
                Log.d(TAG,"File Path " + path);

                Log.d(TAG,"File Name"  + displayName);
                Log.d(TAG,"File Size"  + fileSize);
                Log.d(TAG,"File type"  + fileExt);

            } catch (FileNotFoundException e) {

                e.printStackTrace();
            }
        }
        else if(requestCode == 2 && resultCode == Activity.RESULT_OK)
        {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                Cursor c =  getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String number = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    Log.d(TAG," Selected Contact " + name);
                    // TODO Whatever you want to do with the selected contact name.
                }
            }

        }
    }



}





