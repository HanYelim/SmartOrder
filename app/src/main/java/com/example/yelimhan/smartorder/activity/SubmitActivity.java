package com.example.yelimhan.smartorder.activity;

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
        String option, type, size, mCustomer, name;
        mCustomer = "123qwe";
        int count, price, mindex;
        mNow = System.currentTimeMillis();
        date = new Date(mNow);
        String gettime = sdf.format(date);
        mindex = Integer.valueOf(gettime);


        String str = "";
        for(int i = 0; i < oData.size(); i++){
            name = oData.get(i).mName;
            size = oData.get(i).mSize;
            price = Integer.parseInt(oData.get(i).mPrice);
            type = oData.get(i).mTemp;
            option = oData.get(i).mOption;
            count = oData.get(i).mCount;
            str  = mCustomer + name + type + option + String.valueOf(mindex) + String.valueOf(count) + String.valueOf(price);
            Log.d("str : ", str);

            insertOrderDisposable = ApiService.getMENU_SERVICE().insertOrder(mCustomer, name, size, type, option, mindex, count, price)
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
