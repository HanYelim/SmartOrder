package com.example.yelimhan.smartorder.adapter;

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
import com.example.yelimhan.smartorder.R;

import org.w3c.dom.Text;

import java.util.List;

public class ListAdapter extends ArrayAdapter<OrderItem>  implements View.OnClickListener {

    private Context context;
    private List mList;
    private ListView mListView;

    TextView tvn;
    TextView tvc;
    TextView tvt;
    TextView tvs;
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
            tvn = (TextView)rowView.findViewById(R.id.textname);
            tvc = (TextView)rowView.findViewById(R.id.textcount);
            tvt = (TextView)rowView.findViewById(R.id.texttemp);
            tvs = (TextView)rowView.findViewById(R.id.textsize);
            tvp = (TextView)rowView.findViewById(R.id.textprice);
        }

        OrderItem oi = (OrderItem) mList.get(position);
        tvn.setText(oi.mName);
        tvc.setText(String.valueOf(oi.mCount));
        tvt.setText(oi.mTemp);
        tvs.setText(oi.mSize);
        tvp.setText(oi.mPrice);

        return rowView;
    }

    @Override
    public void onClick(View v) {

    }
}