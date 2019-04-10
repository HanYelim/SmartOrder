package com.example.yelimhan.smartorder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;
import android.content.Intent;

import com.example.yelimhan.smartorder.adapter.ListAdapter;
import com.example.yelimhan.smartorder.adapter.MenuAdapter;
import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.model.Menu;
import com.example.yelimhan.smartorder.network.ApiService;

import java.util.ArrayList;
import java.util.List;

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
    private TextView tvTotal;
    List<Menu> all_menu_list = new ArrayList<>();
    List<OrderItem> oData = new ArrayList<>();
    List<OrderItem>  lastOrders = new ArrayList<>();
    OrderItem item = null;      // 즐겨찾는 메뉴 객체

    private Disposable disposable_favorite, disposable_allmenu, disposable_recent;

    Button btnDelete;
    ListAdapter oAdapter;
    int index = 0;
    Button allMenu, favMenu, lastMenu;
    LinearLayout layout1;
    GridView gridView;
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
                        Log.d(" favorite menu : " , menu.getType() + " " + menu.getName() + "\n" + menu.getSize());
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
                gridView.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
            }
        });
        allMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
            }
        });
        favMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridView.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
            }
        });
        disposable_allmenu = ApiService.getMENU_SERVICE().getMenuList() // 전체메뉴
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<ArrayList<Menu>>() {
                    @Override
                    public void accept(ArrayList<Menu> menus) {
                        for(Menu menu : menus)
                            all_menu_list.add(menu);
                        MenuAdapter menuAdapter = new MenuAdapter(getApplicationContext(), R.layout.menu_item, menus);
                        gridView = (GridView)findViewById(R.id.gridView1);
                        gridView.setAdapter(menuAdapter);

                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Menu menu = all_menu_list.get(i); // 이게 메뉴정보
                                // menu.getName() 하면 이름 넘어옴
                                Log.d("전체 메뉴 중 니가 누른거 : ", menu.getName());
                                //그리드뷰 눌렁승ㄹ대
                                intent = new Intent(getApplicationContext(), ChooseTypeActivity.class);
                                OrderItem o = new OrderItem(menu.getName());
                                intent.putExtra("Object", o);
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


    // 리스트의 삭제 버튼 클릭
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



        oAdapter.notifyDataSetChanged();
        listView.setAdapter(oAdapter);

    }
    class MyListener implements ImageView.OnClickListener {
        @Override
        public void onClick(View v) {
            for(int i=0;i<index;i++){
                if(v.getId() == lastOrderImg[i].getId()){       // 지난 주문의 이미지 클릭
                    if (oData.size() == 0){
                        OrderItem newoi = new OrderItem(lastOrders.get(i).mName,lastOrders.get(i).mCount,lastOrders.get(i).mTemp,lastOrders.get(i).mSize,lastOrders.get(i).mPrice);
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
                            OrderItem newoi = new OrderItem(lastOrders.get(i).mName,lastOrders.get(i).mCount,lastOrders.get(i).mTemp,lastOrders.get(i).mSize,lastOrders.get(i).mPrice);
                            oData.add(newoi);
                        }
                    }
                    oAdapter.notifyDataSetChanged();
                    listView.setAdapter(oAdapter);
                    updateTotalPrice();
                }
            }

            if(v.getId() == favoriteImg.getId()){       // 즐겨찾는 메뉴의 이미지 클릭
                OrderItem temp = new OrderItem(item.mName, item.mCount, item.mTemp, item.mSize, item.mPrice);
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
                    return 10000;


        return 0;
    }
    void updateTotalPrice(){
        int price=0;
        for (OrderItem oi : oData){
            price += Integer.parseInt(oi.mPrice);
            tvTotal.setText("총 가격 : "+price);
        }
    }
}
