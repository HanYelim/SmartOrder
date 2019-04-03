package com.example.yelimhan.smartorder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yelimhan.smartorder.ListAdapter;
import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;

import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends AppCompatActivity {

    private ImageView[] lastOrderImg = new ImageView[5];
    private ListView listView;
    List<OrderItem> oData = new ArrayList<>();

    Button btnDelete;
    ListAdapter oAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        btnDelete = findViewById(R.id.btndelete);
        listView = (findViewById(R.id.listView));
        lastOrderImg[0] = findViewById(R.id.lastOrderImg1);
        lastOrderImg[1] = findViewById(R.id.lastOrderImg2);
        lastOrderImg[2] = findViewById(R.id.lastOrderImg3);
        lastOrderImg[3] = findViewById(R.id.lastOrderImg4);
        lastOrderImg[4] = findViewById(R.id.lastOrderImg5);

        //lastOrderImg[0].setOnClickListener(new MyListener());

        for(int i=0;i<lastOrderImg.length;i++){
            lastOrderImg[i].setOnClickListener(new MyListener());
        }

        final ListAdapter oAdapter = new ListAdapter(SelectActivity.this, oData, listView);
        listView.setAdapter(oAdapter);

        oData.add(new OrderItem("아메리카노", 1, "ICE", "Small", "2000"));
        oData.add(new OrderItem("카페라떼", 1, "ICE", "Large", "3000"));

        // 삭제 버튼
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = listView.getCheckedItemPosition();
                if(pos != ListView.INVALID_POSITION){
                    oData.remove(pos);
                    listView.clearChoices();
                    oAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Toast.makeText(SelectActivity.this, String.valueOf(v.getId()), Toast.LENGTH_SHORT).show();
            for(int i=0;i<lastOrderImg.length;i++){
                if(v.getId() == lastOrderImg[i].getId()){
                    oData.add(new OrderItem("아메리카노", 1, "ICE", "Small", "2000"));
                }
            }

        }
    }
}
