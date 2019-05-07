package com.example.rekog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.amazonaws.services.rekognition.AmazonRekognition;
//import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AmazonRekognition rekognitionClient;


    }
}
