package com.example.yelimhan.smartorder;

import java.io.Serializable;

public class OrderItem implements Serializable{
    public String mName;
    public int mCount;
    public String mTemp;
    public String mSize;
    public String mPrice;
    public String mOption;

    public OrderItem(String _n, int _c, String _t, String _s, String _p, String _o){
        mName = _n;
        mCount = _c;
        mTemp = _t;
        mSize = _s;
        mPrice = _p;
        mOption = _o;
    }

    public OrderItem(String _n){
        mName = _n;
    }

    public OrderItem(){}
}