package com.example.yelimhan.smartorder.activity;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.yelimhan.smartorder.R;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;

import java.util.ArrayList;

public class VoiceActivity extends AppCompatActivity {
    Intent i;
    TextView tv;
    SpeechRecognizer mRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        tv = findViewById(R.id.tv);
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(i);
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override public void onRmsChanged(float rmsdB) {
            // TODO Auto-generated method stub
        }
        @Override public void onReadyForSpeech(Bundle params) {
            // TODO Auto-generated method stub
            Log.d("", "ready");
        }
        @Override public void onPartialResults(Bundle partialResults) {
            // TODO Auto-generated method stub
        }
        @Override public void onEvent(int eventType, Bundle params) {
            // TODO Auto-generated method stub
        }
        @Override public void onError(int error) {
            // TODO Auto-generated method stub
            Log.d("", "error"+String.valueOf(error));
        }
        @Override public void onEndOfSpeech() {
            // TODO Auto-generated method stub
        }
        @Override public void onBufferReceived(byte[] buffer) {
            // TODO Auto-generated method stub
        }
        @Override public void onBeginningOfSpeech() {
            // TODO Auto-generated method stub
        }
        @Override
        public void onResults(Bundle results) {
            Log.d("", "result");
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);

            KeywordExtractor ke = new KeywordExtractor();
            KeywordList kl = ke.extractKeyword(rs[0], true);
            for (int i = 0; i < kl.size(); i++) {
                Keyword kwrd = kl.get(i);
                System.out.println(kwrd.getString() + "\t" + kwrd.getCnt());
            }
        }
    };
}