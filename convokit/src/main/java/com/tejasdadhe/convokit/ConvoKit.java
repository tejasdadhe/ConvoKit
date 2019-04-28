package com.tejasdadhe.convokit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tejasdadhe.convokit.Activities.UsersListActivity;
import com.tejasdadhe.convokit.Modals.Conversation;
import com.tejasdadhe.convokit.Modals.Message;
import com.tejasdadhe.convokit.Modals.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvoKit
{
    private Context context;
    private String firstUserId;

    public ConvoKit(Context context)
    {
        this.context = context;
    }

    public static User getFirstUser() {
         User tejas = new User();
         tejas.setUserId("tejas");
         return tejas;
    }

    public static User getNextUser() {
        User tejas = new User();
        tejas.setUserId("nextUser");
        return tejas;
    }


    public void displayUsersList()
    {
        Intent intent = new Intent(context,UsersListActivity.class);
        context.startActivity(intent);
    }




    public ConversationInstance with(String myUserId)
    {
        this.firstUserId = myUserId;
        return new ConversationInstance();
    }




    public class ConversationInstance
    {
        private String conversationId;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Conversation");
        DatabaseReference convoListRef = FirebaseDatabase.getInstance().getReference("Conversation-List");


        ConversationInstance() {

        }

        public void initialisePrivateConversation(String userId, final ConvoKitTaskListener listener)   //This method will create a conversation if at alread doesnt exist
        {
            String conversationId = ServiceProvider.generatePrivateConversationId(firstUserId,userId);
            final Conversation conversation = new Conversation();
            conversation.setConversationId(conversationId);
            conversation.setTargetUserIds(Arrays.asList(firstUserId,userId));
            conversation.setConversationType(Conversation.CONVERSATION_TYPE_PRIVATE);
            Map<String, Object> userUpdates = new HashMap<>();
            for(int i=0; i < conversation.getTargetUserIds().size(); i++)
            {
                userUpdates.put(conversation.getTargetUserIds().get(i)+"/"+conversationId, conversation);
            }
            convoListRef.updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        listener.OnSuccess(conversation);
                    else
                        listener.OnFailure();
                }
            });
        }


        public void openConversation(@NonNull String conversationId)
        {
            Intent intent = new Intent(context,ConversationActivity.class);
            intent.putExtra("conversation_id",conversationId);
            intent.putExtra("first_user_id",firstUserId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        public void fetchConversationDetails(@NonNull final String conversationId, final ConvoKitTaskListener listener)
        {
            convoListRef.child(firstUserId).child(conversationId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() == null)
                    {
                        throw new ExceptionInInitializerError("Conversation : "+conversationId+" Not found for provided userId : "+ firstUserId +" . Make sure the conversation is initialised at least once.");
                    }
                    else
                    {
                        Conversation conversation = dataSnapshot.getValue(Conversation.class);
                        listener.OnSuccess(conversation);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void sendMessage(Message draftMessage,String conversationId, final ChatServiceProvider.MessageActionListener messageActionListener) {
            DatabaseReference messageference = reference.child(conversationId);
            String id = messageference.push().getKey();
            messageference.child(id).setValue(draftMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                        messageActionListener.OnMessageSendSuccess();
                    else
                        messageActionListener.OnMessageSendFailure();
                }
            });
        }

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }
    }

    public void fetchTargetUsers(List<String> targetUserIds, final ChatServiceProvider.UserFetchListener listener)
    {
        if(targetUserIds.size() > 0)
        {
            final int n = targetUserIds.size();
            final ArrayList<User> users = new ArrayList<>();
            for (int i=0;i<n;i++)
            {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userReference = database.getReference("Users").child(targetUserIds.get(i));
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
                        else
                            listener.OnUserFetchFailed();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.OnUserFetchFailed();
                    }
                });
            }
        }
        else
            throw new IllegalArgumentException("The targetUserIds[] array is empty");
    }



    public interface ConvoKitTaskListener
    {
        public void OnSuccess(Object object);
        public void OnFailure();
    }

}
