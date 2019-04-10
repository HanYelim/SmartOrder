package com.example.yelimhan.smartorder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;

public class OptionActivity extends AppCompatActivity {
    TextView tvCount;
    ImageButton countMinus, countPlus;
    RadioGroup rdshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        final OrderItem o = (OrderItem) getIntent().getSerializableExtra("Object");
        tvCount = findViewById(R.id.count);
        countMinus = findViewById(R.id.count_minus);
        countPlus = findViewById(R.id.count_plus);
        tvCount.setText("1");
        o.mCount = 1;
        countMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(o.mCount > 1){
                    o.mCount--;
                    tvCount.setText(String.valueOf(o.mCount));
                }
            }
        });
        countPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o.mCount++;
                tvCount.setText(String.valueOf(o.mCount));
            }
        });

        rdshot = findViewById(R.id.rgshot);
        rdshot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("아이디", String.valueOf(checkedId));

            }
        });
    }
}
