package com.tejasdadhe.convokit.Activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tejasdadhe.convokit.Adapters.UserAdapter;
import com.tejasdadhe.convokit.ConvoKit;
import com.tejasdadhe.convokit.Modals.Conversation;
import com.tejasdadhe.convokit.Modals.User;
import com.tejasdadhe.convokit.R;

import java.util.ArrayList;

public class UsersListActivity extends AppCompatActivity {

    final String TAG = "Users List Activity";

    ArrayList <User> usersList;
    RecyclerView usersListView;
    UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userReference = database.getReference("Users");

        usersList = new ArrayList<>();
        userAdapter = new UserAdapter(usersList, new UserAdapter.ItemEventsListener() {
            @Override
            public void ItemOnClickListener(int position) {
                final User selectedUser = usersList.get(position);
                Log.d(TAG,selectedUser.getDisplayName());

                final ConvoKit.ConversationInstance myConversationInstance = new ConvoKit(getApplicationContext()).with("tejas");

                myConversationInstance.initialisePrivateConversation(selectedUser.getUserId(), new ConvoKit.ConvoKitTaskListener() {
                    @Override
                    public void OnSuccess(Object object) {
                        myConversationInstance.openConversation(((Conversation) object).getConversationId());
                    }

                    @Override
                    public void OnFailure() {

                    }
                });

            }
            @Override
            public void ItemLongClickListener(int position) {

            }
        });



        usersListView = findViewById(R.id.users_list);
        usersListView.setLayoutManager(new LinearLayoutManager(this));
        usersListView.setAdapter(userAdapter);

        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                User newUser = dataSnapshot.getValue(User.class);
                usersList.add(newUser);
                userAdapter.notifyItemInserted(usersList.size()-1);

            }

            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


    }
}
