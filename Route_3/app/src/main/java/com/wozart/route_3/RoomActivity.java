package com.wozart.route_3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Intent intent = getIntent();
        String message = intent.getStringExtra("room");

        TextView textView = (TextView) findViewById(R.id.tv_message);
        textView.setText(message);
    }
}
