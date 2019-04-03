package com.example.yelimhan.smartorder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yelimhan.smartorder.ListAdapter;
import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.database.LastOrder;
import com.example.yelimhan.smartorder.model.Menu;
import com.example.yelimhan.smartorder.network.ApiService;

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
    List<OrderItem> oData = new ArrayList<>();
    List<OrderItem>  lastOrders = new ArrayList<>();
    OrderItem item = null;      // 즐겨찾는 메뉴 객체

    private Disposable disposable_favorite, disposable_allmenu, disposable_recent;

    Button btnDelete;
    ListAdapter oAdapter;
    int index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select);
        btnDelete = findViewById(R.id.btndelete);
        listView = (findViewById(R.id.listView));

        favoriteImg = findViewById(R.id.favoriteImg);
        favoriteText = findViewById(R.id.favoriteText);
        lastOrderImg[0] = findViewById(R.id.lastOrderImg1);
        lastOrderImg[1] = findViewById(R.id.lastOrderImg2);
        lastOrderImg[2] = findViewById(R.id.lastOrderImg3);
        lastOrderText[0] = findViewById(R.id.lastOrderText1);
        lastOrderText[1] = findViewById(R.id.lastOrderText2);
        lastOrderText[2] = findViewById(R.id.lastOrderText3);

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
                        favoriteText.setText(menu.getName());
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

        //oData.add(new OrderItem("아메리카노", 1, "ICE", "Small", "2000"));
        //oData.add(new OrderItem("카페라떼", 1, "ICE", "Large", "3000"));

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

        }
    }

    int IsSame(OrderItem new_item, OrderItem old_item){
        if(new_item.mName.equals(old_item.mName))
            if(new_item.mSize.equals(old_item.mSize))
                if(new_item.mTemp.equals(old_item.mTemp))
                    return 10000;


         return 0;
    }
}
