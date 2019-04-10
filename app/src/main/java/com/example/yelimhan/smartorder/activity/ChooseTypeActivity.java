package com.example.yelimhan.smartorder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;

public class ChooseTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choos_type);
        OrderItem o = (OrderItem) getIntent().getSerializableExtra("OBJECT");

    }
}
