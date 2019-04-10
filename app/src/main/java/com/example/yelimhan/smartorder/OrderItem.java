package com.example.yelimhan.smartorder;

public class OrderItem {
    public String mName;
    public int mCount;
    public String mTemp;
    public String mSize;
    public String mPrice;

    public OrderItem(String _n,int _c, String _t, String _s,String _p){
        mName = _n;
        mCount = _c;
        mTemp = _t;
        mSize = _s;
        mPrice = _p;
    }
    public OrderItem(){

    }
}