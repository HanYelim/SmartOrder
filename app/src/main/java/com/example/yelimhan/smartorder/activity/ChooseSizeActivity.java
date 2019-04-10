package com.example.yelimhan.smartorder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;

public class ChooseSizeActivity extends AppCompatActivity {
    TextView tv;
    ImageButton large, small;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_size);
        final OrderItem o = (OrderItem) getIntent().getSerializableExtra("Object");
        tv = findViewById(R.id.menu);
        large = findViewById(R.id.large);
        small = findViewById(R.id.small);
        tv.setText(o.mName + " " + o.mTemp);

        large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionActivity.class);
                o.mSize = "large";
                intent.putExtra("Object", o);
                startActivity(intent);
            }
        });

        small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionActivity.class);
                o.mSize = "small";
                intent.putExtra("Object", o);
                startActivity(intent);
            }
        });
    }
}
