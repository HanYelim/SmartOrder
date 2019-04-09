package com.example.yelimhan.smartorder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.yelimhan.smartorder.ListAdapter;
import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.database.LastOrder;
import com.example.yelimhan.smartorder.model.Menu;
import com.example.yelimhan.smartorder.network.ApiService;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SelectActivity extends AppCompatActivity {

    private ImageView[] lastOrderImg = new ImageView[3];
    private TextView[] lastOrderText = new TextView[3];
    private ListView listView;
    private ImageView favoriteImg;
    private TextView favoriteText;
    private TextView tvTotal;
    List<OrderItem> oData = new ArrayList<>();
    List<OrderItem>  lastOrders = new ArrayList<>();
    OrderItem item = null;      // 즐겨찾는 메뉴 객체

    private Disposable disposable_favorite, disposable_allmenu, disposable_recent;

    Button btnDelete;
    ListAdapter oAdapter;
    int index = 0;
    Button allMenu, favMenu, lastMenu;
    LinearLayout layout1, layout2;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select);
        listView = (findViewById(R.id.listView));

        favoriteImg = findViewById(R.id.favoriteImg);
        favoriteText = findViewById(R.id.favoriteText);
        lastOrderImg[0] = findViewById(R.id.lastOrderImg1);
        lastOrderImg[1] = findViewById(R.id.lastOrderImg2);
        lastOrderImg[2] = findViewById(R.id.lastOrderImg3);
        lastOrderText[0] = findViewById(R.id.lastOrderText1);
        lastOrderText[1] = findViewById(R.id.lastOrderText2);
        lastOrderText[2] = findViewById(R.id.lastOrderText3);
        lastMenu = findViewById(R.id.lastMenu);
        allMenu = findViewById(R.id.allMenu);
        favMenu = findViewById(R.id.favMenu);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);

        tvTotal = findViewById(R.id.txttotal);

        disposable_favorite = ApiService.getMENU_SERVICE().getFavoriteMenu("123qwe")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Menu>() {
                    @Override
                    public void accept(Menu menu) throws Exception {
                        String str = "";
                        String resName = "";
                        str += menu.getType();
                        str += menu.getIndex();
                        item = new OrderItem(menu.getName(), 1, menu.getType(), menu.getSize(), menu.getPrice());
                        resName = "@drawable/" + str;
                        int resID = getResources().getIdentifier(resName, "drawable", getPackageName());
                        favoriteImg.setImageResource(resID);
                        favoriteText.setText(menu.getType() + " " + menu.getName() + "\n" + menu.getSize());
                        Log.d(" favorite menu : " , menu.getName());
                    }
                });
        disposable_recent = ApiService.getMENU_SERVICE().getRecentMenu("123qwe")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<ArrayList<Menu>>() {
                    @Override
                    public void accept(ArrayList<Menu> menus) {
                        String str = "";
                        String resName;
                        for (Menu menu : menus) {
                            str = "";
                            str += menu.getType();
                            str += menu.getIndex();
                            lastOrders.add(new OrderItem(menu.getName(), 1, menu.getType(), menu.getSize(), menu.getPrice()));
                            resName = "@drawable/" + str;
                            int resID = getResources().getIdentifier(resName, "drawable", getPackageName());
                            lastOrderText[index].setText(menu.getType() + " " + menu.getName() + "\n" + menu.getSize());
                            lastOrderImg[index++].setImageResource(resID);
                        }
                    }
                }, new io.reactivex.functions.Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.getMessage();
                    }
                });
        lastMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout2.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
            }
        });
        allMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
            }
        });
        favMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout2.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
            }
        });
        disposable_allmenu = ApiService.getMENU_SERVICE().getMenuList() // 전체메뉴
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<ArrayList<Menu>>() {
                    @Override
                    public void accept(ArrayList<Menu> menus) {
                        String str = "";
                        String resName;
                        int resID;
                        for (Menu menu : menus) {
                            str = "";
                            if(menu.getType() == "BOTH"){
                                str += "ice";
                                str += menu.getIndex();
                                resName = "@drawable/" + str;
                                resID = getResources().getIdentifier(resName, "drawable", getPackageName());
                                // 사진 등록
                                str = "";
                                str += "hot";
                                str += menu.getIndex();
                                resName = "@drawable/" + str;
                                resID = getResources().getIdentifier(resName, "drawable", getPackageName());
                                // 사진 등록
                            }
                            else{
                                str += "ice";
                                str += menu.getIndex();
                                resName = "@drawable/" + str;
                                resID = getResources().getIdentifier(resName, "drawable", getPackageName());
                                //사진 등록
                            }
                        }
                    }
                }, new io.reactivex.functions.Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.getMessage();
                    }
                });


        //lastOrderImg[0].setOnClickListener(new MyListener());

        for(int i=0;i<3;i++){
            lastOrderImg[i].setOnClickListener(new MyListener());

        }
        favoriteImg.setOnClickListener(new MyListener());

        oAdapter = new ListAdapter(SelectActivity.this, oData, listView);
        listView.setAdapter(oAdapter);


    }

    class MyListener implements ImageView.OnClickListener {
        @Override
        public void onClick(View v) {
            for(int i=0;i<index;i++){
                if(v.getId() == lastOrderImg[i].getId()){
                    if (oData.size() == 0)
                        oData.add(lastOrders.get(i));
                    else{
                        int same = 0;
                        int j;
                        for(j = 0; j < oData.size(); j++){
                            same = IsSame(lastOrders.get(i), oData.get(j));
                            if(same == 10000)
                                break;
                        }
                        if(same == 10000){
                            oData.get(j).mPrice = String.valueOf(Integer.parseInt(oData.get(j).mPrice)+Integer.parseInt(lastOrders.get(i).mPrice));
                            oData.get(j).mCount++;
                        }
                        else
                            oData.add(lastOrders.get(i));
                    }
                    oAdapter.notifyDataSetChanged();
                    listView.setAdapter(oAdapter);
                    updateTotalPrice();
                }
            }

            if(v.getId() == favoriteImg.getId()){
                OrderItem temp = new OrderItem(item.mName, item.mCount, item.mTemp, item.mSize, item.mPrice);
                if (oData.size() == 0)
                    oData.add(temp);
                else{
                    int same = 0;
                    int j;
                    for(j = 0; j < oData.size(); j++){
                        same = IsSame(temp, oData.get(j));
                        if(same == 10000)
                            break;
                    }
                    if(same == 10000){
                        int a = Integer.parseInt(temp.mPrice);
                        oData.get(j).mPrice = String.valueOf(Integer.parseInt(oData.get(j).mPrice) + a);
                        oData.get(j).mCount++;
                    }
                    else
                        oData.add(temp);
                }

                temp = null;
                oAdapter.notifyDataSetChanged();
                listView.setAdapter(oAdapter);
            }
            oAdapter.notifyDataSetChanged();
            updateTotalPrice();

        }
    }

    int IsSame(OrderItem new_item, OrderItem old_item){
        if(new_item.mName.equals(old_item.mName))
            if(new_item.mSize.equals(old_item.mSize))
                if(new_item.mTemp.equals(old_item.mTemp))
                    return 10000;


        return 0;
    }
    void updateTotalPrice(){
        int price=0;
        Toast.makeText(this, "토탈프라이스", Toast.LENGTH_SHORT).show();
        for (OrderItem oi : oData){
            price += Integer.parseInt(oi.mPrice);
            tvTotal.setText("총 가격 : "+price);

        }
    }
}
