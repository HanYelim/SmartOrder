package com.example.yelimhan.smartorder.activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yelimhan.smartorder.R;

import java.util.ArrayList;
import java.util.Locale;


public class SpeechActivity extends AppCompatActivity {
    TextToSpeech tts;
    SpeechRecognizer stt;
    boolean recordButtonStatus = false;
    Intent intent;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 5);
            toast("권한 허용해주세여");
        }

        setContentView(R.layout.activity_speech);

        final TextView txt;
        txt = findViewById(R.id.textresult);

        txt.setTextSize(18);

        final Button input;
        input = findViewById(R.id.btn);

        input.setText("음성 입력");

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        stt = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i("Test", "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        stt.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(SpeechActivity.this,"음성입력시작",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.i("test", "Ready For Speech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.i("test", "RMS Changed: " + rmsdB);
                progressBar.setProgress((int) rmsdB);
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
                toast("음성입력종료");
            }

            @Override
            public void onError(int error) {
                toast("오류 발생 : " + error);
            }

            @Override
            public void onResults(Bundle results) {
                Log.i("test", "Results");
                ArrayList<String> result = (ArrayList<String>) results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                txt.append("결과 :  "+result.get(0)+"\n");
                toast(result.get(0));
                stt.destroy();
                recordButtonStatus = false;
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recordButtonStatus){
                    recordButtonStatus = false;
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    stt.stopListening();
                }else{
                    stt.startListening(intent);
                    recordButtonStatus = true;
                }
            }
        });
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_RECORD_PERMISSION:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    speech.startListening(recognizerIntent);
//                } else {
//                    Toast.makeText(SpeechActivity.this, "Permission Denied!", Toast
//                            .LENGTH_SHORT).show();
//                }
//        }
//    }

//    private void replyAnswer(String input, TextView txt){
//        try{
//            if(input.equals("?덈뀞")){
//                txt.append("[吏?뒪?? ?꾧뎄?몄슂?\n");
//                tts.speak("?꾧뎄?몄슂?", TextToSpeech.QUEUE_FLUSH, null);
//            }
//            else if(input.equals("?덈뒗 ?꾧뎄??")){
//                    txt.append("[吏?뒪?? ?섎뒗 吏?뒪?곕씪怨???\n");
//            tts.speak("?섎뒗 吏?뒪?곕씪怨???", TextToSpeech.QUEUE_FLUSH, null);
//        }
//            else if(input.equals("醫낅즺")){
//            finish();
//        }
//        else {
//            txt.append("[吏?뒪?? 萸먮씪?붽굅??\n");
//            tts.speak("萸먮씪?붽굅??", TextToSpeech.QUEUE_FLUSH, null);
//        }
//    } catch (Exception e) {
//        toast(e.toString());
//    }


    private void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
