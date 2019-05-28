package com.example.yelimhan.smartorder.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yelimhan.smartorder.OrderItem;
import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.model.Customer;
import com.example.yelimhan.smartorder.model.Menu;
import com.example.yelimhan.smartorder.network.ApiService;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class IdentifyActivity extends AppCompatActivity {
    ByteArrayInputStream inputStream1;
    ArrayList<Integer> url;
    Disposable searchCustomerDisposable;
    SharedPreferences pref;

    private FaceServiceClient faceServiceClient = new FaceServiceRestClient("https://koreacentral.api.cognitive.microsoft.com/face/v1.0", "873449264b7740e3ba3ca53b3c9df99f");
    private final String personGroupId = "smartordergroup";
    Face[] result = null;
    //ByteArrayInputStream inputStream = null;

    Button btn_identify;
    Button btn_re;
    Bitmap orgImage2;


    int i = 0;

    ByteArrayInputStream inputStream2;

    Bitmap orgImage = null;
    Boolean flag = false;
    ProgressDialog mDialog;
    int coupon=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_identify);
        url = getIntent().getIntegerArrayListExtra("url");

        btn_identify = findViewById(R.id.btn_identify);
        btn_re = findViewById(R.id.button_reg);

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/camtest");
        String fileName = String.format("%d.jpg", url.get(0));
        String fileName2 = String.format("%d.jpg", url.get(1));

        orgImage = BitmapFactory.decodeFile(String.valueOf(dir) + "/" + fileName);
        orgImage2 = BitmapFactory.decodeFile(String.valueOf(dir) + "/" + fileName2);

        mDialog = new ProgressDialog(IdentifyActivity.this);

        btn_identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 0;
                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                if(rotation == Surface.ROTATION_270){
                    orgImage = rotateImage(orgImage,180);
                    orgImage2 = rotateImage(orgImage2,180);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                orgImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
                orgImage2.compress(Bitmap.CompressFormat.PNG, 100, outputStream2);
                inputStream2 = new ByteArrayInputStream(outputStream2.toByteArray());

                new detectTask().execute(inputStream);
            }
        });

        btn_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    class detectTask extends AsyncTask<InputStream, String, Face[]> {

        @Override
        protected Face[] doInBackground(InputStream... inputStreams) {
            try {
                publishProgress("인식중입니다...");
                result = faceServiceClient.detect(inputStreams[0], true, false, null);

                if (result == null) {
                    return null;
                } else {
                    Log.d("daf result", String.valueOf(result.length));
                    return result;
                }

            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            mDialog.dismiss();
            if (result != null) {
                if (result.length == 0) {    // 인식 안됨
                    if (flag == false) {      // 첫번째
                        i++;
                        new detectTask().execute(inputStream2);
                        flag = true;
                    } else {       // 두번째
                        Toast.makeText(getApplicationContext(), "얼굴이 인식되지 않았습니다. 처음으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {       // detect 성공
                    final UUID[] faceIds = new UUID[result.length];
                    for (int i = 0; i < result.length; i++) {
                        faceIds[i] = result[i].faceId;
                    }
                    new IdentificationTask(personGroupId).execute(faceIds);

                    Log.d("i : ", String.valueOf(i));
                }
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mDialog.setMessage(values[0]);
        }
    }

    private class IdentificationTask extends AsyncTask<UUID, String, IdentifyResult[]> {
        String personGroupId;
        private boolean mSucceed = true;

        public IdentificationTask(String personGroupId) {
            this.personGroupId = personGroupId;
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }

        @Override
        protected void onPostExecute(IdentifyResult[] identifyResults) {
            if (mDialog != null)
                mDialog.dismiss();
            if (mSucceed) {
                if (identifyResults.length != 0 && identifyResults[0].candidates.size() != 0) {
                    Log.d("train complete", String.valueOf(mSucceed));
                    Log.d("identify result", String.valueOf(identifyResults[0].faceId));
                    new PersonDetectionTask(personGroupId).execute(identifyResults[0].candidates.get(0).personId);  // get person
                } else {
                    Toast.makeText(getApplicationContext(), "얼굴이 등록되지 않았어요.\n 얼굴을 먼저 등록해주세요.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(IdentifyActivity.this, CameraActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... uuids) {
            try {
                publishProgress("Identifying...");
                return faceServiceClient.identity(personGroupId,
                        uuids   // face ids
                        , 1);    // max number of candidates returned
            } catch (Exception e) {
                mSucceed = false;
                e.printStackTrace();
                return null;
            }
        }

        ;
    }

    // get person
    class PersonDetectionTask extends AsyncTask<UUID, String, Person> {
        private String personGroupId;

        public PersonDetectionTask(String personGroupId) {
            this.personGroupId = personGroupId;
        }

        @Override
        protected Person doInBackground(UUID... uuids) {
            try {
                return faceServiceClient.getPerson(personGroupId, uuids[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Person person) {
            //ImageView img = findViewById(R.id.image_detect_view);
            //img.setImageBitmap(drawFaceRectangleOnBitmap(mBitmap, facesDetected,person.name));

            if (person != null) {
                searchCustomerDisposable = ApiService.getCUSTOMER_SERVICE().getCustomerInfo(person.personId.toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Customer>() {
                            @Override
                            public void accept(Customer customer) throws Exception {
                                person.name = customer.getNickname();
                                coupon = customer.getCoupon();
                                pref = getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("Customer_nickname", person.name);
                                editor.putString("Customer_ID", String.valueOf(person.personId));
                                editor.putInt("Customer_coupon", coupon);
                                editor.commit();
                            }
                        });

                pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Customer_nickname", person.name);
                editor.putString("Customer_ID", String.valueOf(person.personId));
                editor.commit();
                Toast.makeText(getApplicationContext(), person.name + " 님", Toast.LENGTH_LONG).show();

                new AddFaceTask().execute(person.personId);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("personId", person.personId);
                startActivity(intent);
                finish();
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
        }
    }

    class AddFaceTask extends AsyncTask<UUID, String, Boolean> {


        @Override
        protected Boolean doInBackground(UUID... params) {
            // Get an instance of face service client to detect faces in image.
            try {
                UUID personId = params[0];

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                Log.d("test", String.valueOf(i));
                if (i == 0)
                    orgImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                else if (i == 1)
                    orgImage2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                else
                    Log.d("add face", "wrong");
                InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());

                ///// 얼굴 고르기 체크
                FaceRectangle faceRect = result[0].faceRectangle;
                Log.d("new face", faceRect.toString());

                // Start the request to add face.
                AddPersistedFaceResult addFaceResult = faceServiceClient.addPersonFace(
                        personGroupId,
                        personId,
                        imageInputStream,
                        "User data",
                        faceRect);
                return true;
            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.e("add face error", e.toString());
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }

    @Override
    protected void onDestroy() {
        mDialog.dismiss();
        super.onDestroy();
    }

    // 이미지 회전 함수
    public Bitmap rotateImage(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }
}
