package com.example.yelimhan.smartorder.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.model.Menu;

import org.w3c.dom.Text;

import java.util.List;

public class MenuAdapter extends BaseAdapter {
    Context context;
    int layout;
    List<Menu> menus;
    LayoutInflater inf;
    public MenuAdapter(Context context, int layout, List<Menu> menus){
        this.context = context;
        this.layout = layout;
        this.menus = menus;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public Object getItem(int i) {
        return menus.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Integer.parseInt(menus.get(i).getIndex());
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = inf.inflate(layout, null);

        String str = "";
        String resName;
        int resID;
        str = "";
        str += "ice";
        str += menus.get(i).getIndex();
        resName = "@drawable/" + str;
        Log.d("resId : ", resName);
        resID = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
        ImageView imageView = (ImageView)view.findViewById(R.id.menu_item_img);
        TextView textView = (TextView)view.findViewById(R.id.menu_item_txt);
        imageView.setImageResource(resID);
        textView.setText(menus.get(i).getName());
        return view;
        // 사진 등록
    }
}
