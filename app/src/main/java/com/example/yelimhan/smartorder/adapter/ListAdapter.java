package com.example.yelimhan.smartorder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<OrderItem>  implements View.OnClickListener {
    private ListBtnClickListener listBtnClickListener;
    private Context context;
    private List mList;
    private ListView mListView;

    TextView tvn;
    TextView tvc;
    TextView tvp;
    TextView tvo;
    ImageButton btnDelete;

    ArrayList<OrderItem> sub = new ArrayList<>();

    ArrayList<ArrayList<OrderItem>> items = new ArrayList<ArrayList<OrderItem>>();

    public interface ListBtnClickListener{
        void onListBtnClick(int position);
    }

    public ListAdapter(Context context,
                       List<OrderItem> list,
                       ListView listview,
                       ListBtnClickListener clickListener
    ) {
        super(context, 0, list);

        this.context = context;
        this.mList = list;
        this.mListView = listview;
        this.listBtnClickListener = clickListener;
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
            tvp = (TextView)rowView.findViewById(R.id.textprice);
            tvo = (TextView)rowView.findViewById(R.id.textoption);

            btnDelete = (ImageButton)rowView.findViewById(R.id.deletebtn);
            btnDelete.setOnClickListener(this);
            btnDelete.setTag(position);
        }

        OrderItem oi = (OrderItem) mList.get(position);
        tvn.setText(oi.mName);
        tvc.setText(String.valueOf(oi.mCount) + "개");
        tvp.setText(oi.mPrice + "원");

        String opt = "";
        if(!oi.mSize.equals("")){
            opt += "  └ " + oi.mSize +"\n";
        }
        if(!oi.mTemp.equals("")){
            opt += "  └ " + oi.mTemp;
        }
        tvo.setText(opt);


        return rowView;
    }

    @Override
    public void onClick(View v) {
        if(this.listBtnClickListener != null){
            this.listBtnClickListener.onListBtnClick((int)v.getTag());
        }
    }
}