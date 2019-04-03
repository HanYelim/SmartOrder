package com.example.yelimhan.smartorder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;

import org.w3c.dom.Text;

import java.util.List;

public class ListAdapter extends ArrayAdapter<OrderItem> {

    private Context context;
    private List mList;
    private ListView mListView;

    TextView tvn;
    TextView tvp;

    public ListAdapter(Context context,
                       List<OrderItem> list,
                       ListView listview
    ) {
        super(context, 0, list);

        this.context = context;
        this.mList = list;
        this.mListView = listview;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parentViewGroup){
        View rowView = convertView; // 코드 가독성을 위해서 rowView 변수를 사용합니다.

        String Status;
        if(rowView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            rowView = layoutInflater.inflate(R.layout.listview_item, parentViewGroup, false);
            tvn = (TextView)rowView.findViewById(R.id.textTitle);
            tvp = (TextView)rowView.findViewById(R.id.textDate);

        }

        OrderItem oi = (OrderItem) mList.get(position);
        tvn.setText(oi.mName);
        tvp.setText(oi.mPrice);

        Log.d("test in listadapter",oi.mName);

        return rowView;
    }

}