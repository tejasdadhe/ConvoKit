package com.tejasdadhe.convokit;

import android.content.Context;
import android.content.Intent;

import com.tejasdadhe.convokit.Activities.UsersListActivity;

public class ConvoKit
{
    private Context context;

    public ConvoKit(Context context)
    {
        this.context = context;
    }

    public void displayUsersList()
    {
        Intent intent = new Intent(context,UsersListActivity.class);
        context.startActivity(intent);
    }

}
