package com.example.yelimhan.smartorder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;

public class ChooseTypeActivity extends AppCompatActivity {
    Button tv;
    ImageButton ice, hot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);
        final OrderItem o = (OrderItem) getIntent().getSerializableExtra("Object");
        tv = findViewById(R.id.menu);
        tv.setText(o.mName);
        ice = findViewById(R.id.ice);
        hot = findViewById(R.id.hot);

        ice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseSizeActivity.class);
                o.mTemp = "ice";
                intent.putExtra("Object", o);
                startActivity(intent);
            }
        });

        hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseSizeActivity.class);
                o.mTemp = "hot";
                intent.putExtra("Object", o);
                startActivity(intent);
            }
        });
    }
}
