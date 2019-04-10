package com.example.yelimhan.smartorder.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;

public class OptionActivity extends AppCompatActivity {
    TextView tvCount;
    ImageButton countMinus, countPlus;
    RadioGroup rdshot;
    RadioGroup rgShot, rgSyrup,rgIce;
    String result_count = "";
    String result_shot = "";
    String result_syrup = "";
    String result_ice = "";
    RadioButton[] btn_rgs;
    RadioButton[] btn_rgss;
    RadioButton[] btn_rgi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        final OrderItem o = (OrderItem) getIntent().getSerializableExtra("Object");
        tvCount = findViewById(R.id.count);
        countMinus = findViewById(R.id.count_minus);
        countPlus = findViewById(R.id.count_plus);
        rgShot = (RadioGroup) findViewById(R.id.rgshot);
        rgSyrup = (RadioGroup) findViewById(R.id.rgsyrup);
        rgIce = (RadioGroup) findViewById(R.id.rgice);


        tvCount.setText("1");
        o.mCount = 1;
        countMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (o.mCount > 1) {
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

        int[] rgs_id = {R.id.rgs1, R.id.rgs2, R.id.rgs3, R.id.rgs4, R.id.rgs5};
        btn_rgs = new RadioButton[5];
        for (int i = 0; i < btn_rgs.length; i++) {
            btn_rgs[i] = (RadioButton)findViewById(rgs_id[i]);
        }
        rgShot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < btn_rgs.length; i++) {
                    if (btn_rgs[i].isChecked())
                        btn_rgs[i].setTextColor(Color.parseColor("#ffffff"));
                    else
                        btn_rgs[i].setTextColor(Color.parseColor("#0c0c0c"));
                }
            }
        });
//        int[] rgss_id = {R.id.rgss1, R.id.rgss2, R.id.rgss3};
//        btn_rgss = new RadioButton[5];
//        for (int i = 0; i < btn_rgss.length; i++) {
//            btn_rgss[i] = (RadioButton)findViewById(rgss_id[i]);
//        }
//        rgSyrup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                for (int i = 0; i < btn_rgss.length; i++) {
//                    if (btn_rgss[i].isChecked())
//                        btn_rgss[i].setTextColor(Color.parseColor("#ffffff"));
//                    else
//                        btn_rgss[i].setTextColor(Color.parseColor("#0c0c0c"));
//                }
//            }
//        });
//        int[] rgi_id = {R.id.rgi1, R.id.rgi2, R.id.rgi3};
//        btn_rgi = new RadioButton[5];
//        for (int i = 0; i < btn_rgi.length; i++) {
//            btn_rgi[i] = (RadioButton)findViewById(rgi_id[i]);
//        }
//        rgIce.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                for (int i = 0; i < btn_rgi.length; i++) {
//                    if (btn_rgi[i].isChecked())
//                        btn_rgi[i].setTextColor(Color.parseColor("#ffffff"));
//                    else
//                        btn_rgi[i].setTextColor(Color.parseColor("#0c0c0c"));
//                }
//            }
//        });
    }


}










