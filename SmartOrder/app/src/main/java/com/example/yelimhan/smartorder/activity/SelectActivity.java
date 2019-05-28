package com.example.yelimhan.smartorder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.adapter.ListAdapter;
import com.example.yelimhan.smartorder.adapter.MenuAdapter;
import com.example.yelimhan.smartorder.model.Menu;
import com.example.yelimhan.smartorder.network.ApiService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SelectActivity extends AppCompatActivity implements ListAdapter.ListBtnClickListener {

    private ImageView[] lastOrderImg = new ImageView[3];
    private TextView[] lastOrderText = new TextView[3];
    private ListView listView;
    private ImageView favoriteImg;
    private TextView favoriteText;
    private TextView tvTotal, select_text;
    List<Menu> rec_menu_list = new ArrayList<>();
    List<OrderItem> oData = new ArrayList<>();
    List<OrderItem> lastOrders = new ArrayList<>();
    OrderItem item = null;      // 즐겨찾는 메뉴 객체
    List<Integer> num_list;

    private Disposable disposable_favorite, disposable_allmenu, disposable_recent;

    Button btnSubmit;
    ListAdapter oAdapter;
    int index = 0;
    Button allMenu, lastMenu;
    LinearLayout layout1, layout_all;
    GridView gridView, re_gridView;
    Intent intent, submit_intent;
    OrderItem voiceO;
    int coupon = 0;
    TextView tvCoupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_select_new);

        listView = (findViewById(R.id.listView));
        tvCoupon = findViewById(R.id.cou_text);
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
        //favMenu = findViewById(R.id.favMenu);
        layout1 = findViewById(R.id.layout1);
        layout_all = findViewById(R.id.allMenu_layout);
        btnSubmit = findViewById(R.id.submit_btn);
        select_text = findViewById(R.id.select_view_text);
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        boolean flag = pref.getBoolean("Voice", false);
        String customer_nickname = pref.getString("Customer_nickname", "");
        String customer_ID = pref.getString("Customer_ID", "");
        coupon = pref.getInt("Customer_coupon",0);
        Log.d("customer nick : ", customer_nickname);
        Log.d("customer id : ", customer_ID);
        select_text.setText(customer_nickname + " 님을 위한 추천 메뉴");
        OrderItem Data;

        if(flag){
            Data = (OrderItem) getIntent().getSerializableExtra("order");
            oData.add(Data);
        }

        tvTotal = findViewById(R.id.txttotal);

        /// 쿠폰 받아오기
        tvCoupon.append(String.valueOf(coupon));

        disposable_favorite = ApiService.getMENU_SERVICE().getFavoriteMenu(customer_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Menu>() {
                    @Override
                    public void accept(Menu menu) throws Exception {
                        String str = "";
                        String resName = "";
                        str += menu.getType().toLowerCase();
                        str += menu.getIndex();
                        item = new OrderItem(menu.getName(), 1, menu.getType(), menu.getSize(), menu.getPrice(), menu.getOpt());
                        resName = "@drawable/" + str;
                        int resID = getResources().getIdentifier(resName, "drawable", getPackageName());
                        favoriteImg.setImageResource(resID);
                        favoriteText.setText(menu.getType() + " " + menu.getName() + "\n" + menu.getSize());
                        //Log.d(" favorite menu : " , menu.getType().toLowerCase() + " " + menu.getName() + "\n" + menu.getSize());
                    }
                });
        disposable_recent = ApiService.getMENU_SERVICE().getRecentMenu(customer_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<ArrayList<Menu>>() {
                    @Override
                    public void accept(ArrayList<Menu> menus) {
                        String str = "";
                        String resName;
                        for (Menu menu : menus) {
                            str = "";
                            str += menu.getType().toLowerCase();
                            str += menu.getIndex();
                            lastOrders.add(new OrderItem(menu.getName(), 1, menu.getType(), menu.getSize(), menu.getPrice(), menu.getOpt()));
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
                layout_all.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
            }
        });
        allMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.GONE);
                layout_all.setVisibility(View.VISIBLE);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() { // 전송 누르면 결제창으로 넘어간다냥
            @Override
            public void onClick(View view) {
                submit_intent = new Intent(getApplicationContext(), SubmitActivity.class);
                submit_intent.putExtra("menuList", (Serializable) oData);
                startActivityForResult(submit_intent, 1000);
            }
        });

        final Random random = new Random();
        disposable_allmenu = ApiService.getMENU_SERVICE().getMenuList() // 전체메뉴
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<ArrayList<Menu>>() {
                    @Override
                    public void accept(final ArrayList<Menu> menus) {
                        for(int i = 0; i < 4; i++){
                            if(rec_menu_list.size() == 0)
                                rec_menu_list.add(menus.get(random.nextInt(menus.size())));
                            else{
                                int index, count;
                                count = 0;
                                index = random.nextInt(menus.size());
                                for(int j = 0; j < rec_menu_list.size(); j++){
                                    if(menus.get(index).getIndex() == rec_menu_list.get(j).getIndex())
                                        count = -1;
                                }
                                if(count != -1)
                                    rec_menu_list.add(menus.get(index));
                            }
                            if(rec_menu_list.size() != i + 1)
                                i--;
                        }
                        MenuAdapter menuAdapter = new MenuAdapter(getApplicationContext(), R.layout.menu_item, menus);
                        MenuAdapter menuAdapter2 = new MenuAdapter(getApplicationContext(), R.layout.menu_item, rec_menu_list);
                        gridView = (GridView)findViewById(R.id.gridView1);
                        re_gridView = (GridView)findViewById(R.id.re_menu_grid);
                        gridView.setAdapter(menuAdapter);
                        re_gridView.setAdapter(menuAdapter2);

                        updateTotalPrice();

                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Menu menu = menus.get(i); // 이게 메뉴정보
                                // menu.getName() 하면 이름 넘어옴
                                //Log.d("전체 메뉴 중 니가 누른거 : ", menu.getName());
                                //그리드뷰 눌렁승ㄹ대
                                OrderItem o = new OrderItem(menu.getName());
                                o.mPrice = menu.getPrice();
                                if(!menu.getType().equals("BOTH")){
                                    o.mTemp = menu.getType();
                                    if(!menu.getSize().equals("BOTH")){
                                        o.mSize = menu.getSize();
                                        intent = new Intent(getApplicationContext(), OptionActivity.class);
                                    }
                                    else{
                                        intent = new Intent(getApplicationContext(), ChooseSizeActivity.class);
                                    }
                                }
                                else{
                                    intent = new Intent(getApplicationContext(), ChooseTypeActivity.class);
                                }
                                intent.putExtra("Object", o);
                                intent.putExtra("menuList", (Serializable) oData);
                                intent.putExtra("option", menu.getOpt());
                                startActivityForResult(intent, 1000);
                            }
                        });
                        re_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Menu menu = rec_menu_list.get(i); // 이게 메뉴정보
                                // menu.getName() 하면 이름 넘어옴
                                //Log.d("전체 메뉴 중 니가 누른거 : ", menu.getName());
                                //그리드뷰 눌렁승ㄹ대
                                OrderItem o = new OrderItem(menu.getName());
                                o.mPrice = menu.getPrice();
                                if(!menu.getType().equals("BOTH")){
                                    o.mTemp = menu.getType();
                                    if(!menu.getSize().equals("BOTH")){
                                        o.mSize = menu.getSize();
                                        intent = new Intent(getApplicationContext(), OptionActivity.class);
                                    }
                                    else{
                                        intent = new Intent(getApplicationContext(), ChooseSizeActivity.class);
                                    }
                                }
                                else{
                                    intent = new Intent(getApplicationContext(), ChooseTypeActivity.class);
                                }
                                intent.putExtra("Object", o);
                                intent.putExtra("menuList", (Serializable) oData);
                                intent.putExtra("option", menu.getOpt());
                                startActivityForResult(intent, 1000);
                            }
                        });
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
        oAdapter = new ListAdapter(SelectActivity.this, oData, listView, this);
        listView.setAdapter(oAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1000) {
            OrderItem o = (OrderItem) data.getSerializableExtra("object");
            int same = 0, j;
            for(j = 0; j < oData.size(); j++){
                same = IsSame(o, oData.get(j));
                if(same == 10000)
                    break;
            }
            if(same == 10000){
                oData.get(j).mPrice = String.valueOf((Integer.parseInt(oData.get(j).mPrice)+Integer.parseInt(o.mPrice)*o.mCount));
                oData.get(j).mCount += o.mCount;
            }
            else{
                OrderItem newoi = new OrderItem(o.mName,o.mCount,o.mTemp,
                        o.mSize,String.valueOf(Integer.parseInt(o.mPrice)*o.mCount), o.mOption);
                oData.add(newoi);
            }

            updateTotalPrice();
            oAdapter.notifyDataSetChanged();
            listView.setAdapter(oAdapter);
        }

        if(requestCode == 1234){
            voiceO = (OrderItem) data.getSerializableExtra("order");
            oData.add(voiceO);
            oAdapter.notifyDataSetChanged();
            listView.setAdapter(oAdapter);
        }
    }

    // 리스트의 삭제 버튼 클릭
    @Override
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
    class MyListener implements ImageView.OnClickListener {
        @Override
        public void onClick(View v) {
            for(int i=0;i<index;i++){
                if(v.getId() == lastOrderImg[i].getId()){       // 지난 주문의 이미지 클릭
                    if (oData.size() == 0){
                        OrderItem newoi = new OrderItem(lastOrders.get(i).mName,lastOrders.get(i).mCount,lastOrders.get(i).mTemp,
                                lastOrders.get(i).mSize,lastOrders.get(i).mPrice, lastOrders.get(i).mOption);
                        oData.add(newoi);
                    }
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
                        else{
                            OrderItem newoi = new OrderItem(lastOrders.get(i).mName,lastOrders.get(i).mCount,lastOrders.get(i).mTemp,
                                    lastOrders.get(i).mSize,lastOrders.get(i).mPrice, lastOrders.get(i).mOption);
                            oData.add(newoi);
                        }
                    }
                    oAdapter.notifyDataSetChanged();
                    listView.setAdapter(oAdapter);
                    updateTotalPrice();
                }
            }

            if(v.getId() == favoriteImg.getId()){       // 즐겨찾는 메뉴의 이미지 클릭
                OrderItem temp = new OrderItem(item.mName, item.mCount, item.mTemp, item.mSize, item.mPrice, item.mOption);
                if (oData.size() == 0){
                    oData.add(temp);
                }
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
                    else{
                        oData.add(temp);
                    }
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
                    if(new_item.mOption.equals(old_item.mOption))
                        return 10000;


        return 0;
    }
    void updateTotalPrice(){
        int price = 0;
        for (OrderItem oi : oData){
            price += Integer.parseInt(oi.mPrice);
        }
        tvTotal.setText("총 가격 : " + price);
    }
}
