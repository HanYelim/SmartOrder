package com.example.yelimhan.smartorder.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.network.ApiService;
import com.example.yelimhan.smartorder.network.model.BaseResponse;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SubmitActivity extends AppCompatActivity {
    private Disposable insertOrderDisposable;
    ArrayList<OrderItem> oData;
    TextView textView;
    long mNow;
    Date date;
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        textView = (TextView)findViewById(R.id.submit_text);
        oData = (ArrayList<OrderItem>) getIntent().getSerializableExtra("menuList");
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String customer_nickname = pref.getString("Customer_nickname", "");
        String customer_ID = pref.getString("Customer_ID", "");
        String option, type, size, mCustomer, name;
        int count, price, mindex;
        mNow = System.currentTimeMillis();
        date = new Date(mNow);
        String gettime = sdf.format(date);
        mindex = Integer.valueOf(gettime);


        String str = "";
        for(int i = 0; i < oData.size(); i++){
            name = oData.get(i).mName;
            size = oData.get(i).mSize;
            count = oData.get(i).mCount;
            price = Integer.parseInt(oData.get(i).mPrice) / count;
            count = 1;
            type = oData.get(i).mTemp;
            option = oData.get(i).mOption;
            str  = customer_ID + name + type + option + String.valueOf(mindex) + String.valueOf(count) + String.valueOf(price);
            Log.d("str : ", str);

            insertOrderDisposable = ApiService.getMENU_SERVICE().insertOrder(customer_ID, name, size, type, option, mindex, count, price)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<BaseResponse>() {
                        @Override
                        public void accept(BaseResponse baseResponse) {
                            Log.d("update", baseResponse.status);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {

                        }
                    });
        }
    }
}
