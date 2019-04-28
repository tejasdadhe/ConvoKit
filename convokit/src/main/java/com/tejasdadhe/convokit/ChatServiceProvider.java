package com.tejasdadhe.convokit;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tejasdadhe.convokit.Modals.Conversation;
import com.tejasdadhe.convokit.Modals.Message;
import com.tejasdadhe.convokit.Modals.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ChatServiceProvider {
    private DatabaseReference conversationReference, conversationListReference, userReference;

    private Conversation conversation;


    private String TAG = " Service Provider";
    FirebaseDatabase database;

    public ChatServiceProvider(String  conversationId)
    {
        this.conversation = conversation;
        database = FirebaseDatabase.getInstance();
        conversationReference = database.getReference("Messages").child(conversationId);
        conversationListReference = database.getReference("ConversationList").child("tejas").child(conversationId);
    }




    public void fetchConversationData(String conversationId, final ConversationFetchListener listener)
    {
        DatabaseReference databaseReference = database.getReference("ConversationList").child(conversationId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    listener.OnSuccess(dataSnapshot.getValue(Conversation.class));
                }
                else
                    listener.OnFailure();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.OnFailure();
            }
        });
    }


    public void fetchTargetUsers(List<String> targetUserIds, final UserFetchListener listener)
    {
        if(targetUserIds.size() > 0)
        {
            final int n = targetUserIds.size();
            final ArrayList<User> users = new ArrayList<>();
            for (int i=0;i<n;i++)
            {
                userReference = database.getReference("Users").child(targetUserIds.get(i));
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null)
                        {
                            User user = dataSnapshot.getValue(User.class);
                            users.add(user);
                            if(users.size() >= n){
                                listener.OnUserFetchCompleted(users.toArray(new User[0]));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        else
            throw new IllegalArgumentException("The targetUserIds[] array is empty");
    }

    public void sendMessage(Message message, final MessageActionListener messageActionListener)
    {
        @NonNull String messageId = conversationReference.push().getKey();
        Log.d(TAG,"Message ID set as" + messageId);


        message.setMessageID(messageId)
                .setSenderId("tejas")
                .setMessageStatus(Message.MESSAGE_STATUS_PENDING);

        conversationReference.child(messageId).setValue(message, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if(databaseError == null)
                {
                    Log.d(TAG,"Message sending successful");
                    messageActionListener.OnMessageSendSuccess();
                }
                else
                {
                    Log.d(TAG,"Message sending failed");
                    messageActionListener.OnMessageSendFailure();
                }
            }
        });

        List<String> userIds = conversation.getTargetUserIds();
        for (String userId : userIds) {
            database.getReference("ConversationList").child(userId).setValue(conversation);
        }
    }



    public void uploadFile(Uri fileUri, final FileUploadListener fileUploadListener)
    {
        Log.d(TAG," Upload Started ");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();

        final StorageReference ref = storageRef.child("files/" + UUID.randomUUID().toString());

        UploadTask uploadTask = ref.putFile(fileUri);



        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                fileUploadListener.OnFileUploadProgress(progress);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                fileUploadListener.OnFileUploadPaused();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                fileUploadListener.OnFileUploadFaiure();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String downloadUrl =  uri.toString();
                    fileUploadListener.OnFileUploadSuccess(downloadUrl);
                    Log.d(TAG, "onSuccess: uri= "+ downloadUrl);
                }
            });
        }
    });


    }


    public void recordAudio()
    {

    }

    public interface AudioRecordEventListener
    {

        void OnAudioRecordStarted();
        void OnAudioRecordStoppped();
    }


    public interface MessageActionListener
    {
        void OnMessageSendSuccess();
        void OnMessageSendFailure();
    }


    public interface FileUploadListener
    {
        void OnFileUploadProgress(double progress);
        void OnFileUploadPaused();
        void OnFileUploadSuccess(String downloadUri);
        void OnFileUploadFaiure();
    }


    public void fetchMessages(final MessageFetchingListener messageFetchingListener)
    {


        Query lastQuery = conversationReference.child("mp").orderByKey();
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String message = dataSnapshot.child("message").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Handle possible errors.
            }
        });


        final ArrayList<Message> messageArrayList = new ArrayList<>();



        conversationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                ArrayList<Message> arrayList = new ArrayList<>();

                Log.d(TAG," Recieved SnapShot : " + dataSnapshot.getChildrenCount());



                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Message message = snapshot.getValue(Message.class);
                    arrayList.add(message);


                }
                messageFetchingListener.OnMessageFetchSuccessful(arrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                Log.d(TAG," Fetch Failure ");
                messageFetchingListener.OnMesssageFetchFailure();
            }
        });
    }



    public void fetchConversationDetails(String conversationId)
    {

    }




    void fetchPreviousMessages(int start, int end)
    {



    }

    public interface MessageFetchingListener
    {
        void OnMessageFetchSuccessful(ArrayList<Message> arrayList);
        void OnMesssageFetchFailure();
    }



    public void registerForMessageEvents(final MessageEventListener messageEventListener)
    {
        conversationReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Message recievedMessage = dataSnapshot.getValue(Message.class);
                    messageEventListener.OnNewMessageRecieved(recievedMessage);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d(TAG,dataSnapshot.toString());
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    messageEventListener.OnListenerError(databaseError.toString());
                }
            }
        );

    }


    public interface MessageEventListener
    {
        void OnNewMessageRecieved(Message recievedMessage);
        void OnMessageDeleted();
        void OnMessageRead();
        void OnMessageDelivered();
        void OnListenerError(String error);
    }


    public interface ConversationFetchListener
    {
        void OnSuccess(Conversation conversation);
        void OnFailure();
    }



    public interface UserFetchListener
    {
        void OnUserFetchCompleted(User[] users);
        void OnUserFetchFailed();
        void OnUserChanged(User newUser);
    }


    public  static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


}
