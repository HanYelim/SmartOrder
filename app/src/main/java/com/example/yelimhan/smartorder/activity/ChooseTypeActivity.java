package com.example.yelimhan.smartorder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.adapter.ListAdapter;

import java.util.ArrayList;

public class ChooseTypeActivity extends AppCompatActivity implements ListAdapter.ListBtnClickListener {
    Button tv;
    ImageButton ice, hot;
    ListView listView;
    ListAdapter oAdapter;
    ArrayList<OrderItem> oData;
    TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose_type);
        final OrderItem o = (OrderItem) getIntent().getSerializableExtra("Object");
        oData = (ArrayList<OrderItem>) getIntent().getSerializableExtra("menuList");
        tv = findViewById(R.id.menu);
        tv.setText(o.mName);
        ice = findViewById(R.id.ice);
        hot = findViewById(R.id.hot);
        listView = findViewById(R.id.listView);
        tvTotal = findViewById(R.id.txttotal);

        ice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseSizeActivity.class);
                o.mTemp = "ice";
                intent.putExtra("Object", o);
                intent.putExtra("menuList", oData);
                startActivity(intent);
                finish();
            }
        });

        hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseSizeActivity.class);
                o.mTemp = "hot";
                intent.putExtra("Object", o);
                intent.putExtra("menuList", oData);
                startActivity(intent);
                finish();
            }
        });

        oAdapter = new ListAdapter(ChooseTypeActivity.this, oData, listView, this);
        listView.setAdapter(oAdapter);
    }

    @Override
    public void onListBtnClick(int position) {
        OrderItem temp = new OrderItem(oData.get(position).mName , oData.get(position).mCount , oData.get(position).mTemp, oData.get(position).mSize, oData.get(position).mPrice );

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
        int price=0;
        for (OrderItem oi : oData){
            price += Integer.parseInt(oi.mPrice);
        }
        tvTotal.setText("총 가격 : " + price);
    }
}
