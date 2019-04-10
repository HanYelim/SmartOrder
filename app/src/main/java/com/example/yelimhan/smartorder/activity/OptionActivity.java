package com.example.yelimhan.smartorder.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.adapter.ListAdapter;

import java.util.ArrayList;

public class OptionActivity extends AppCompatActivity implements ListAdapter.ListBtnClickListener {
    TextView tvCount, tvTotal;
    ListView listView;
    ListAdapter oAdapter;
    ArrayList<OrderItem> oData;
    ImageButton countMinus, countPlus;
    RadioGroup rgShot, rgSyrup,rgIce;
    String result_count = "";
    String result_shot = "";
    String result_syrup = "";
    String result_ice = "";
    RadioButton[] btn_rgs;
    RadioButton[] btn_rgss;
    RadioButton[] btn_rgi;
    Button menu, cart;
    String option;
    String result_opt = "";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_option);
        final OrderItem o = (OrderItem) getIntent().getSerializableExtra("Object");
        oData = (ArrayList<OrderItem>) getIntent().getSerializableExtra("menuList");
        intent = getIntent();
        option = getIntent().getStringExtra("option");
        tvCount = findViewById(R.id.count);
        countMinus = findViewById(R.id.count_minus);
        countPlus = findViewById(R.id.count_plus);
        rgShot = (RadioGroup) findViewById(R.id.rgshot);
        rgSyrup = (RadioGroup) findViewById(R.id.rgsyrup);
        rgIce = (RadioGroup) findViewById(R.id.rgice);
        listView = findViewById(R.id.listView);
        tvTotal = findViewById(R.id.txttotal);
        menu = findViewById(R.id.menu);
        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result_opt = "";
                int shot, syrup, ice;

                // 개수
                int count = Integer.parseInt(tvCount.getText().toString());

                // 샷
                if(option.equals("111"))
                    shot = rgShot.indexOfChild(findViewById(rgShot.getCheckedRadioButtonId()));
                else
                    shot = -1;
                result_opt += String.valueOf(shot) + " ";

                // 시럽
                if(option.equals("011"))
                    syrup = rgSyrup.indexOfChild(findViewById(rgSyrup.getCheckedRadioButtonId()));
                else
                    syrup = -1;
                result_opt += String.valueOf(syrup) + " ";

                //얼음
                if(option.equals("001"))
                    ice = rgIce.indexOfChild(findViewById(rgIce.getCheckedRadioButtonId()));
                else
                    ice = -1;

                result_opt += String.valueOf(ice);

                Log.d("opt test : ", result_opt);
                o.mOption = result_opt;
                o.mCount = count;
                intent.putExtra("object", o);
                setResult(1000, intent);
                finish();
            }
        });

        tvCount.setText("1");
        menu.setText("\"" + o.mName + " " + o.mTemp + " " + o.mSize + "\"의 옵션을 선택해 주세요.");
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
        int[] rgss_id = {R.id.rgss1, R.id.rgss2, R.id.rgss3};
        btn_rgss = new RadioButton[3];
        for (int i = 0; i < btn_rgss.length; i++) {
            btn_rgss[i] = (RadioButton)findViewById(rgss_id[i]);
        }
        rgSyrup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < btn_rgss.length; i++) {
                    if (btn_rgss[i].isChecked())
                        btn_rgss[i].setTextColor(Color.parseColor("#ffffff"));
                    else
                        btn_rgss[i].setTextColor(Color.parseColor("#0c0c0c"));
                }
            }
        });
        int[] rgi_id = {R.id.rgi1, R.id.rgi2, R.id.rgi3};
        btn_rgi = new RadioButton[3];
        for (int i = 0; i < btn_rgi.length; i++) {
            btn_rgi[i] = (RadioButton)findViewById(rgi_id[i]);
        }
        rgIce.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < btn_rgi.length; i++) {
                    if (btn_rgi[i].isChecked())
                        btn_rgi[i].setTextColor(Color.parseColor("#ffffff"));
                    else
                        btn_rgi[i].setTextColor(Color.parseColor("#0c0c0c"));
                }
            }
        });

        if(option.equals("011")){
            for (int i = 0; i < btn_rgs.length; i++) {
                btn_rgs[i].setClickable(false);
                btn_rgs[i].setBackground(getDrawable(R.drawable.gray_round));
            }
        }
        else if(option.equals("001")){
            for (int i = 0; i < btn_rgs.length; i++) {
                btn_rgs[i].setClickable(false);
                btn_rgs[i].setBackground(getDrawable(R.drawable.gray_round));
            }
            for(int i = 0; i < btn_rgss.length; i++){
                btn_rgss[i].setClickable(false);
                btn_rgss[i].setBackground(getDrawable(R.drawable.gray_round));
            }
        }
        if(o.mTemp.equals("HOT")){
            for(int i = 0; i < btn_rgi.length; i++){
                btn_rgi[i].setClickable(false);
                btn_rgi[i].setBackground(getDrawable(R.drawable.gray_round));
            }
        }

        oAdapter = new ListAdapter(OptionActivity.this, oData, listView, this);
        listView.setAdapter(oAdapter);
        updateTotalPrice();
    }

    public void onListBtnClick(int position) {
        OrderItem temp = new OrderItem(oData.get(position).mName , oData.get(position).mCount , oData.get(position).mTemp,
                oData.get(position).mSize, oData.get(position).mPrice, oData.get(position).mOption );

        // mCount 업데이트
        oData.get(position).mCount -= 1;
        temp.mCount -= 1;
        if(temp.mCount == 0)
            oData.remove(position);
        else{
            oData.get(position).mPrice = String.valueOf(Integer.parseInt(temp.mPrice)/(temp.mCount+1) * (temp.mCount));
        }

        updateTotalPrice();
        oAdapter.notifyDataSetChanged();
        listView.setAdapter(oAdapter);
    }

    void updateTotalPrice(){
        int price = 0;
        for (OrderItem oi : oData){
            price += Integer.parseInt(oi.mPrice);
        }
        tvTotal.setText("총 가격 : " + price);
    }
}










