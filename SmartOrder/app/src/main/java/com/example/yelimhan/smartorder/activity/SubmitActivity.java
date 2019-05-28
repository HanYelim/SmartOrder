package com.example.yelimhan.smartorder.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.cardemulation.CardEmulation;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

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

    int coupon = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        textView = (TextView)findViewById(R.id.submit_text);
        oData = (ArrayList<OrderItem>) getIntent().getSerializableExtra("menuList");
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String customer_nickname = pref.getString("Customer_nickname", "");
        String customer_ID = pref.getString("Customer_ID", "");
        coupon = pref.getInt("Customer_coupon",0);
        String option, type, size, mCustomer, name;
        int count, price, mindex;
        mNow = System.currentTimeMillis();
        date = new Date(mNow);
        String gettime = sdf.format(date);
        mindex = Integer.valueOf(gettime);


        String str = "";

        coupon++;

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

            // 쿠폰 customer에 넣기

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
            AlertDialog.Builder ab = new AlertDialog.Builder(SubmitActivity.this);
            ab.setMessage(Html.fromHtml("주문이 완료되었습니다.")+ "\n쿠폰 개수 : "+ String.valueOf(coupon));
            ab.setPositiveButton("ok", yesButtonClickListener);
            ab.show();

        }
    }
    private DialogInterface.OnClickListener yesButtonClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            Intent intent = new Intent(SubmitActivity.this, CameraActivity.class);
            startActivity(intent);
            finish();
        }

    };
}
