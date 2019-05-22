package com.example.yelimhan.smartorder.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.network.ApiService;
import com.example.yelimhan.smartorder.network.model.BaseResponse;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.Person;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RegisterFaceActivity extends AppCompatActivity {

    private Button cameraButton;
    private Button addPersonBtn;
    private Button detectPersonBtn;
    private TextView textView;
    private EditText editPersonName;

    private FaceServiceClient faceServiceClient = new FaceServiceRestClient("https://koreacentral.api.cognitive.microsoft.com/face/v1.0", "873449264b7740e3ba3ca53b3c9df99f");
    private final String personGroupId = "smartordergroup";
    private static int RESULT_CAPTURE_IMAGE = 0;

    public static Bitmap mBitmap;
    Face[] result = null;
    ByteArrayInputStream inputStream = null;

    Uri imageUri;
    File photoFile;
    ImageView iv1;
    Disposable insertCustomerDisposable;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CAPTURE_IMAGE && resultCode == RESULT_OK)
        {
             mBitmap = (Bitmap) data.getExtras().get("data");

            ///mBitmap = getBitmapFromUri(imageUri);
//            textView.setText("등록된 사진이 있습니다.");
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            try {
//                InputStream in = new FileInputStream(photoFile);
//                BitmapFactory.decodeStream(in, null, options);
//                in.close();
//                in = null;
//            } catch ( Exception e ) {
//                e.printStackTrace();
//            }
//
//            final int width = options.outWidth;
//            final int height = options.outHeight;
//
//            BitmapFactory.Options imgOptions = new BitmapFactory.Options();
//            imgOptions.inSampleSize = 2;
//
//
//            //mBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), imgOptions);
//            mBitmap = BitmapFactory.decodeFile(String.valueOf(photoFile));
            iv1.setImageBitmap(mBitmap);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Initlasing the Views
        cameraButton = (Button) findViewById(R.id.camera_open_btn);
        addPersonBtn = (Button) findViewById(R.id.add_person);
        detectPersonBtn = findViewById(R.id.detect_person);
        editPersonName = (EditText) findViewById(R.id.editpersonname);
        textView = (TextView)findViewById(R.id.check_photo);
        iv1 = findViewById(R.id.testiv);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //captureCamera();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
            }
        });

        detectPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                new detectTask().execute(inputStream);
            }
        });

        addPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());


                // 사람 추가
                new AddPersonToGroupTask(personGroupId, editPersonName.getText().toString()).execute(inputStream);
                new TrainingTask(personGroupId).execute(personGroupId);


            }
        });
    }

    //카메라 작동
    private void captureCamera(){

        String state = Environment.getExternalStorageState();

        //외장메모리 검사
        if(Environment.MEDIA_MOUNTED.equals(state)){

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(intent.resolveActivity( getPackageManager()) != null){

                photoFile = null;

                try{
                    photoFile = createImageFile();
                }catch(IOException e){}

                if(photoFile != null){

                    //getUriForFile 의 두번째 인자는 매니패스트 provider의 authorites와 일치해야한다
                    Uri providerURI = FileProvider.getUriForFile( getApplicationContext() , getPackageName() , photoFile);
                    imageUri = providerURI;

                    //인텐트 전달 할때는 FileProvider의 return값인 Content://로만 , providerURI 값에 카메라 데이터를 넣어보냄
                    intent.putExtra(MediaStore.EXTRA_OUTPUT , providerURI);

                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(intent,RESULT_CAPTURE_IMAGE);
                }
            }
        }else{
            return;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory()+"/Pictures" , "pic");

        if(!storageDir.exists()){
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imageFileName);

        return imageFile;
    }

    /* Uri를 Bitmap으로 쓸 수 있게해준다. */
    private Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    RegisterFaceActivity.this.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class AddPersonToGroupTask extends AsyncTask<InputStream, String, CreatePersonResult> {

        private String personGroupId;
        private String personName;

        public AddPersonToGroupTask(String personGroupId, String personName) {
            this.personGroupId = personGroupId;
            this.personName = personName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(CreatePersonResult person) {
            Toast.makeText(getApplicationContext(),"등록 완료 : "+ personName + String.valueOf(person.personId),Toast.LENGTH_SHORT).show();


            String s = String.valueOf(person.personId);
            insertCustomerDisposable = ApiService.getCUSTOMER_SERVICE().insertCustomer(editPersonName.getText().toString(), s)
                    .subscribeOn(Schedulers.io()) // 회원가입쓰
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<BaseResponse>() {
                        @Override
                        public void accept(BaseResponse baseResponse) {
                            Log.d("update", baseResponse.status);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                        }
                    });

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected CreatePersonResult doInBackground(InputStream... inputStreams) {
            try {
                faceServiceClient.getPersonGroup(personGroupId);
                CreatePersonResult person = faceServiceClient.createPerson(personGroupId,personName, "");

                Log.d("person id : ", person.personId.toString()+"//"+ String.valueOf(result[0].faceRectangle.height));
                faceServiceClient.addPersonFace(personGroupId,person.personId,inputStreams[0],"persontest",result[0].faceRectangle); ///

                return person;
            } catch (Exception ex) {
                Log.e("error create person: ",ex.toString());
                return null;
            }
        }
    }

    class detectTask extends AsyncTask<InputStream, String, Face[]>{

        private ProgressDialog mDialog = new ProgressDialog(RegisterFaceActivity.this);

        @Override
        protected Face[] doInBackground(InputStream... inputStreams) {
            try {
                publishProgress("Detecting...");
                result = faceServiceClient.detect(inputStreams[0], true, false, null);
                if(result == null){
                    return null;
                }
                else{
                    Log.d("daf result",String.valueOf(result.length)+result[0].faceId);

                    return result;
                }

            }
            catch (Exception ex){
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
            //facesDetected = faces;

            if(result.length == 0)
                Toast.makeText(getApplicationContext(),"detect failed" , Toast.LENGTH_SHORT).show();
            else{
                Toast.makeText(getApplicationContext(),"detect finished - " +String.valueOf(result.length), Toast.LENGTH_SHORT).show();
                // 사람 추가


            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mDialog.setMessage(values[0]);
        }
    }

    class TrainingTask extends AsyncTask<String, String, String> {
        private String personGroupId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String string) {
            if (string != null) {
                Log.d("training" ,  string + " training completed");
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        public TrainingTask(String personGroupId) {
            this.personGroupId = personGroupId;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                faceServiceClient.trainPersonGroup(personGroupId);
                Log.d("test : ", "trainging request");
                return params[0];
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

