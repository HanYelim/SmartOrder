package com.example.yelimhan.smartorder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends AppCompatActivity {

    private ImageView[] lastOrderImg = new ImageView[5];
    private ListView listView;
    List<OrderItem> oData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        listView = (findViewById(R.id.listView));
        lastOrderImg[0] = findViewById(R.id.lastOrderImg1);
        lastOrderImg[1] = findViewById(R.id.lastOrderImg2);
        lastOrderImg[2] = findViewById(R.id.lastOrderImg3);
        lastOrderImg[3] = findViewById(R.id.lastOrderImg4);
        lastOrderImg[4] = findViewById(R.id.lastOrderImg5);

        lastOrderImg[0].setOnClickListener(new MyListener());

    }

    class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            oData.add(new OrderItem("아메리카노", "1"));
            Toast.makeText(SelectActivity.this, "되는거니?", Toast.LENGTH_SHORT).show();

            ListAdapter oAdapter = new ListAdapter(SelectActivity.this, oData, listView);
            listView.setAdapter(oAdapter);
        }
    }
}
