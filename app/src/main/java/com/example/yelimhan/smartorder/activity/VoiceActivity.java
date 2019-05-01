package com.example.yelimhan.smartorder.activity;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.adapter.MenuAdapter;
import com.example.yelimhan.smartorder.model.Menu;
import com.example.yelimhan.smartorder.network.ApiService;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.snu.ids.kkma.ma.MorphemeAnalyzer;
import org.snu.ids.kkma.ma.Sentence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class VoiceActivity extends AppCompatActivity {
    Intent i;
    TextView tv;
    Disposable disposable;
    SpeechRecognizer mRecognizer;
    List<Menu> all_menu = new ArrayList<>();
    OrderItem o;
    ArrayList<String> NNG, VA, XR, VV, NNP, NR, MDN;

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
        MorphemeAnalyzer ma = new MorphemeAnalyzer();
        ma.createLogger(null);
        NNG = new ArrayList<>();
        VA = new ArrayList<>();
        XR = new ArrayList<>();
        VV = new ArrayList<>();
        NNP = new ArrayList<>();
        NR = new ArrayList<>();
        MDN = new ArrayList<>();
        o = new OrderItem();

        disposable = ApiService.getMENU_SERVICE().getMenuList() // 전체메뉴 받아오기 all_menu에 다있음
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<ArrayList<Menu>>() {
                    @Override
                    public void accept(final ArrayList<Menu> menus) {
                        for(int i = 0; i < menus.size(); i++){
                            all_menu.add(menus.get(i));
                            Log.d("all menu list : ", String.valueOf(all_menu.size()));
                        }
                    }
                });

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

            MorphemeAnalyzer ma = new MorphemeAnalyzer();
            ma.createLogger(null);
            try {
                List ret = ma.analyze(rs[0]);
                ret = ma.postProcess(ret);
                ret = ma.leaveJustBest(ret);
                List stl = ma.divideToSentences(ret);
                for(int i = 0; i < stl.size(); i++){
                    Sentence st = (Sentence) stl.get(i);
                    for(int j = 0; j < st.size(); j++){
                        //st.get(j).getExp() 따뜻하게
                        tv.append(st.get(j).toString() + " " + st.get(j).getFirstMorp().getTag() + "\n");
                        String tag = st.get(j).getFirstMorp().getTag();
                        String word = st.get(j).getExp();
                        if(tag.equals("NNG")){ //음료 종류
                            NNG.add(word);
                        }// VA, XR, VV, NNP, NR, MDN;
                        else if(tag.equals("VA")) //차갑 크 작 차 뜨겁 따시
                            VA.add(word);
                        else if(tag.equals("XR")) //따뜻 시원
                            XR.add(word);
                        else if(tag.equals("VV")) // 차 + ㄴ
                            VV.add(word);
                        else if(tag.equals("NNP")) //라지 스몰 아이스 핫
                            NNP.add(word);
                        else if(tag.equals("NR")) //수
                            NR.add(word);
                        else if(tag.equals("MDN")) //수
                            MDN.add(word);
                    }
                }
                checkMenu();
                tv.append("\n\n" + String.valueOf(o.mCount));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    void checkMenu(){
        String num;
        if(NR.size() != 0)
            num = NR.get(0);
        else if(MDN.size() != 0)
            num = MDN.get(0);
        else
            num = "";

        if(num.equals("하나") || num.equals("한"))
            o.mCount = 1;
        else if(num.equals("2") || num.equals("두") || num.equals("둘"))
            o.mCount = 2;
        else if(num.equals("세") || num.equals("석"))
            o.mCount = 3;
        else if(num.equals("네") || num.equals("4") || num.equals("넉"))
            o.mCount = 4;
        else if(num.equals("다섯") || num.equals("5"))
            o.mCount = 5;
        else if(num.equals("6") || num.equals("여섯"))
            o.mCount = 6;
        else if(num.equals("7") || num.equals("일곱"))
            o.mCount = 7;
        else if(num.equals("8") || num.equals("여덟"))
            o.mCount = 8;
        else if(num.equals("9") || num.equals("아홉"))
            o.mCount = 9;
    }
}