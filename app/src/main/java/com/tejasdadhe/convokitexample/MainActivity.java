package com.tejasdadhe.convokitexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tejasdadhe.convokit.ConvoKit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button usersList = findViewById(R.id.userList);



        final ConvoKit convoKit = new ConvoKit(this);


        usersList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convoKit.displayUsersList();
            }
        });







    }
}
