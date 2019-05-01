package com.example.yelimhan.smartorder.activity;

import android.content.Intent;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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
    Intent i, next;
    TextView tv, myMenu;
    Disposable disposable;
    SpeechRecognizer mRecognizer;
    SpeechRecognizer reRecognizer;
    List<Menu> all_menu = new ArrayList<>();
    int price;
    OrderItem o, oi;
    Button button;
    ArrayList<String> NNG, VA, XR, VV, NNP, NR, MDN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        tv = findViewById(R.id.tv);
        myMenu = findViewById(R.id.myMenu);
        button = findViewById(R.id.button);
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        reRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        reRecognizer.setRecognitionListener(relistener);
        mRecognizer.startListening(i);
        next = new Intent(getApplicationContext(), SelectActivity.class);

        MorphemeAnalyzer ma = new MorphemeAnalyzer();
        ma.createLogger(null);
        NNG = new ArrayList<>();
        VA = new ArrayList<>();
        XR = new ArrayList<>();
        VV = new ArrayList<>();
        NNP = new ArrayList<>();
        NR = new ArrayList<>();
        MDN = new ArrayList<>();
        o = new OrderItem("");

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VA.clear();
                XR.clear();
                VV.clear();
                NNP.clear();
                NR.clear();
                MDN.clear();
                NNG.clear();
                o = null;
                o = new OrderItem("");

                mRecognizer.destroy();
                reRecognizer.destroy();
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                mRecognizer.setRecognitionListener(listener);
                reRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                reRecognizer.setRecognitionListener(relistener);
                mRecognizer.startListening(i);
            }
        });
    }

    private RecognitionListener relistener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
        }

        @Override
        public void onResults(Bundle results) {
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
                        //tv.append(st.get(j).toString() + " " + st.get(j).getFirstMorp().getTag() + "\n");
                        String tag = st.get(j).getFirstMorp().getTag();
                        String word = st.get(j).getExp();
                        if(tag.equals("NNG")){ //음료 종류
                            NNG.add(word);
                        }// VA, XR, VV, NNP, NR, MDN;
                        else if(tag.equals("VA")) //
                            VA.add(word);
                        else if(tag.equals("XR"))
                            XR.add(word);
                        else if(tag.equals("VV"))
                            VV.add(word);
                        else if(tag.equals("NNP"))
                            NNP.add(word);
                        else if(tag.equals("NR"))
                            NR.add(word);
                        else if(tag.equals("MDN"))
                            MDN.add(word);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if(o.mCount == 0){
                checkMenu();
            }
            if(o.mTemp.equals("BOTH")){
                ner();
            }
            if(o.mSize.equals("BOTH")){
                ner();
            }

            if(o.mCount == 0){
                myMenu.setText(o.mName + " 의 수량을 말해주세요.");
            }
            if(o.mSize.equals("BOTH")){
                myMenu.setText(o.mName + " 의 사이즈를 말해주세요.");
            }
            if(o.mTemp.equals("BOTH")){
                myMenu.setText(o.mName + " 가 따뜻한건지 차가운건지 말해주세요.");
            }

            if(o.mCount == 0 || o.mTemp.equals("BOTH") || o.mSize.equals("BOTH")){
                VA.clear();
                XR.clear();
                VV.clear();
                NNP.clear();
                NR.clear();
                MDN.clear();
                reRecognizer.startListening(i);
            }

            if(o.mCount != 0 && !o.mTemp.equals("BOTH") && !o.mSize.equals("BOTH")){
                o.mPrice = String.valueOf(o.mCount * price);
                next.putExtra("order", (Serializable) o);
                myMenu.setText("됐다능..");
                startActivityForResult(next, 1234);
                //startActivity(next);
                finish();
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

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
                        //tv.append(st.get(j).toString() + " " + st.get(j).getFirstMorp().getTag() + "\n");
                        String tag = st.get(j).getFirstMorp().getTag();
                        String word = st.get(j).getExp();
                        if(tag.equals("NNG")){ //음료 종류
                            NNG.add(word);
                        }// VA, XR, VV, NNP, NR, MDN;
                        else if(tag.equals("VA")) //
                            VA.add(word);
                        else if(tag.equals("XR"))
                            XR.add(word);
                        else if(tag.equals("VV"))
                            VV.add(word);
                        else if(tag.equals("NNP"))
                            NNP.add(word);
                        else if(tag.equals("NR"))
                            NR.add(word);
                        else if(tag.equals("MDN"))
                            MDN.add(word);
                    }
                    ////// 여기부터 이름 찾아서 넣음
                    ////// 이름 넣으면 o르 이름으로 생성함
                    ////// 없는거면 "" 라고 넣어놔씀
                    boolean flag = false;
                    for(int a = 0; a < NNG.size(); a++){
                        if(flag == false){
                            for(int b = 0; b < all_menu.size(); b++){
                                if (flag == false) {
                                    if (all_menu.get(b).getName().equals(NNG.get(a))) {
                                        o = new OrderItem(NNG.get(a));
                                        o.mSize = all_menu.get(b).getSize();
                                        o.mTemp = all_menu.get(b).getType();
                                        o.mOption = all_menu.get(b).getOpt();
                                        price = Integer.parseInt(all_menu.get(b).getPrice());
                                        String opt = "";
                                        if(o.mOption.charAt(0) == '1'){
                                            opt += "1 ";
                                        }
                                        else{
                                            opt += "-1 ";
                                        }

                                        if(o.mOption.charAt(1) == '1'){
                                            opt += "0 ";
                                        }
                                        else{
                                            opt += "-1 ";
                                        }

                                        if(o.mOption.charAt(2) == '1'){
                                            opt += "1";
                                        }
                                        else{
                                            opt += "-1";
                                        }
                                        o.mOption = opt;

                                        flag = true;
                                        break;
                                    } else if (all_menu.get(b).getName().contains(NNG.get(a))) { // 포함한다면
                                        for (int c = a + 1; c < NNG.size(); c++) {
                                            if (all_menu.get(b).getName().contains(NNG.get(c))) {
                                                o = new OrderItem(all_menu.get(b).getName());
                                                o.mSize = all_menu.get(b).getSize();
                                                o.mTemp = all_menu.get(b).getType();
                                                o.mOption = all_menu.get(b).getOpt();
                                                price = Integer.parseInt(all_menu.get(b).getPrice());
                                                String opt = "";
                                                if(o.mOption.charAt(0) == '1'){
                                                    opt += "1 ";
                                                }
                                                else{
                                                    opt += "-1 ";
                                                }

                                                if(o.mOption.charAt(1) == '1'){
                                                    opt += "0 ";
                                                }
                                                else{
                                                    opt += "-1 ";
                                                }

                                                if(o.mOption.charAt(2) == '1'){
                                                    opt += "1";
                                                }
                                                else{
                                                    opt += "-1";
                                                }
                                                o.mOption = opt;
                                                flag = true;
                                                break;
                                            }
                                        }
                                    }
                                    else{
                                        o = new OrderItem("");
                                    }
                                }
                            }
                        }
                    }
                    Log.d("메뉴네임 : ", o.mName);
                    tv.append(o.mName);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if(!o.mName.equals("")){ // 음료들어왔으면
                checkMenu();
                ner();
                if(o.mTemp.equals("BOTH")){
                    // 온도 넣어야 하면
                    myMenu.setText(o.mName + " 가 따뜻한건지 차가운건지 말해주세요");
                    VA.clear();
                    XR.clear();
                    VV.clear();
                    NNP.clear();
                    NR.clear();
                    MDN.clear();
                    reRecognizer.startListening(i);
                }
                if(o.mSize.equals("BOTH")){
                    // 사이즈도 넣어야 하면
                    myMenu.setText(o.mName + " 의 사이즈를 말해주세요");
                    VA.clear();
                    XR.clear();
                    VV.clear();
                    NNP.clear();
                    NR.clear();
                    MDN.clear();
                    reRecognizer.startListening(i);
                }
                if(o.mCount == 0){
                    // 수량없으면
                    myMenu.setText(o.mName + " 의 수량을 말해주세요");
                    VA.clear();
                    XR.clear();
                    VV.clear();
                    NNP.clear();
                    NR.clear();
                    MDN.clear();
                    reRecognizer.startListening(i);
                }
                tv.append(o.mTemp + o.mSize);
            }else{
                // 다시 말하셈

                mRecognizer.startListening(i);
            }

            if(o.mCount != 0 && !o.mTemp.equals("BOTH") && !o.mSize.equals("BOTH")){
                o.mPrice = String.valueOf(o.mCount * price);
                next.putExtra("order", (Serializable) o);
                startActivityForResult(next, 1234);
                finish();
                myMenu.setText("됐다능..");

            }
        }
    };

    void checkMenu(){
        String num;
        o.mCount = 0;
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
        tv.append("\n\n" + String.valueOf(o.mCount));
    }

    void ner() {
        if(o.mTemp.equals("BOTH")){
            for (int i = 0; i < NNP.size(); i++) {
                if (NNP.get(i).contains("아이스"))
                    o.mTemp = "ICE";
                else if (NNP.get(i).contains("핫"))
                    o.mTemp = "HOT";
            }
            for (int i = 0; i < VA.size(); i++) {
                if (VA.get(i).equals("차갑게")
                        || VA.get(i).equals("차게")
                        || VA.get(i).equals("차갑게")
                        || VA.get(i).equals("차가운"))
                    o.mTemp = "ICE";
                else if (VA.get(i).equals("뜨겁게")
                        || VA.get(i).equals("뜨거운"))
                    o.mTemp = "HOT";
            }
            for (int i = 0; i < VV.size(); i++) {
                if (VV.get(i).equals("찬"))
                    o.mTemp = "ICE";
            }
            for (int i=0;i<XR.size();i++){
                if(XR.get(i).contains("따뜻"))
                    o.mTemp = "HOT";
                else if(XR.get(i).contains("시원"))
                    o.mTemp = "ICE";
            }
        }
        if(o.mSize.equals("BOTH")) {
            for (int i = 0; i < NNP.size(); i++) {
                if (NNP.get(i).contains("라지"))
                    o.mSize = "LARGE";
                else if (NNP.get(i).contains("스몰"))
                    o.mSize = "SMALL";
            }
            for (int i = 0; i < VA.size(); i++) {
                if (VA.get(i).contains("작"))
                    o.mSize = "SMALL";
                else if (VA.get(i).contains("크")
                        || VA.get(i).contains("큰"))
                    o.mSize = "LARGE";
            }
        }



        Toast.makeText(this, o.mName+" "+o.mTemp +" " +o.mSize + " " + o.mCount, Toast.LENGTH_SHORT).show();
        tv.append(o.mTemp + " " + o.mSize);
    }
}

