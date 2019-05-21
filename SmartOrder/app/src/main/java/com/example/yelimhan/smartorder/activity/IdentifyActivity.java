package com.example.yelimhan.smartorder.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;

public class IdentifyActivity extends AppCompatActivity {
    ArrayList<Integer> url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        url = getIntent().getIntegerArrayListExtra("url");

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/camtest");
        int name = url.get(0);
        String fileName = String.format("%d.jpg", name);
        Bitmap orgImage = BitmapFactory.decodeFile(String.valueOf(sdCard) + String.valueOf(dir) + fileName);
    }
}
