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
    List<LastOrder> lastOrders = new ArrayList<>();
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

        for(int i=0;i<index;i++){
            lastOrderImg[i].setOnClickListener(new MyListener());

        }

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

            //Toast.makeText(SelectActivity.this, "토스트", Toast.LENGTH_SHORT).show();
            Log.d("getid", String.valueOf(v.getId()));

            for(int i=0;i<index;i++){
                if(v.getId() == lastOrderImg[i].getId()){
                    Log.d("lastordergetid", String.valueOf(lastOrderImg[i].getId()));
                    //oData.add(new OrderItem("아메리카노", 1, "ICE", "Small", "2000"));
                }

            }
            if(v.getId() == favoriteImg.getId()){
                Toast.makeText(SelectActivity.this,"즐찾클릭",Toast.LENGTH_SHORT).show();
                oData.add(item);
                oAdapter.notifyDataSetChanged();
            }


        }
    }
}
