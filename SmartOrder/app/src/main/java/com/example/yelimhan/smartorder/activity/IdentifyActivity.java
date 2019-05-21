package com.example.yelimhan.smartorder.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yelimhan.smartorder.R;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.IdentifyResult;
import com.microsoft.projectoxford.face.contract.Person;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class IdentifyActivity extends AppCompatActivity {
    ByteArrayInputStream inputStream1;
    ArrayList<Integer> url;

    private ImageView iv1;
    private ImageView iv2;
    private ImageView iv3;

    private FaceServiceClient faceServiceClient = new FaceServiceRestClient("https://koreacentral.api.cognitive.microsoft.com/face/v1.0", "873449264b7740e3ba3ca53b3c9df99f");
    private final String personGroupId = "smartordergroup";
    Face[] result = null;
    //ByteArrayInputStream inputStream = null;

    Button btn_identify;

    Bitmap orgImage2;
    Bitmap orgImage3;

    int i =0;

    ByteArrayInputStream inputStream2;
    ByteArrayInputStream inputStream3;


    Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        url =  getIntent().getIntegerArrayListExtra("url");

        iv1 = (ImageView) findViewById(R.id.iv1);

        btn_identify = findViewById(R.id.btn_identify);
       // iv1.setImageBitmap(orgImage);


        // ss -> inputstream

        //inputStream1 = new ByteArrayInputStream(ss.getBytes());
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/camtest");
        String fileName = String.format("%d.jpg", url.get(0));
        String fileName2 = String.format("%d.jpg", url.get(1));
        String fileName3 = String.format("%d.jpg", url.get(2));


        Bitmap orgImage = BitmapFactory.decodeFile(String.valueOf(dir) + "/" + fileName);
        orgImage2 = BitmapFactory.decodeFile(String.valueOf(dir) + "/" + fileName2);
        orgImage3 = BitmapFactory.decodeFile(String.valueOf(dir) + "/" + fileName3);




        btn_identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                orgImage.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
                orgImage2.compress(Bitmap.CompressFormat.PNG,100,outputStream2);
                 inputStream2 = new ByteArrayInputStream(outputStream2.toByteArray());

                ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
                orgImage2.compress(Bitmap.CompressFormat.PNG,100,outputStream3);
                inputStream3 = new ByteArrayInputStream(outputStream3.toByteArray());


                new detectTask().execute(inputStream);
            }
        });

    }

    class detectTask extends AsyncTask<InputStream, String, Face[]> {


        @Override
        protected Face[] doInBackground(InputStream... inputStreams) {
            try {
                i++;
                publishProgress("Detecting...");
                result = faceServiceClient.detect(inputStreams[0], true, false, null);
                if(result == null){
                    return null;
                }
                else{
                    Log.d("daf result",String.valueOf(result.length));
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
           // mDialog.show();
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            // mDialog.dismiss();
            if (result.length == 0){
                Toast.makeText(getApplicationContext(), "detect failed  " + String.valueOf(i), Toast.LENGTH_SHORT).show();

                if(flag == false){
                    new detectTask().execute(inputStream2);
                    flag = true;
                }
            }

            else{

                Toast.makeText(getApplicationContext(),"detect finished  "+ String.valueOf(i), Toast.LENGTH_SHORT).show();

                final UUID[] faceIds = new UUID[result.length];
                for (int i=0;i<result.length;i++){
                    faceIds[i] = result[i].faceId;
                }
                new IdentificationTask(personGroupId).execute(faceIds);
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
          //  mDialog.setMessage(values[0]);
        }
    }

    private class IdentificationTask extends AsyncTask<UUID, String, IdentifyResult[]>{
        String personGroupId;
        private boolean mSucceed = true;

        //private ProgressDialog mDialog = new ProgressDialog(IdentifyActivity.this);
        public IdentificationTask(String personGroupId) {
            this.personGroupId = personGroupId;
        }

        @Override
        protected void onPreExecute() {
        //    mDialog.show();
        }

        @Override
        protected void onPostExecute(IdentifyResult[] identifyResults) {
       //     mDialog.dismiss();
            if(mSucceed){
                Log.d("train complete",String.valueOf(mSucceed));
                Log.d("identify result", String.valueOf(identifyResults[0].faceId));
                new PersonDetectionTask(personGroupId).execute(identifyResults[0].candidates.get(0).personId);  // get person
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... uuids) {
            try{
                publishProgress("Identifying...");
                return faceServiceClient.identity(personGroupId,
                        uuids   // face ids
                        ,1);    // max number of candidates returned
            } catch (Exception e) {
                mSucceed = false;
                e.printStackTrace();
                return null;
            }
        };
    }

    // get person
    class PersonDetectionTask extends AsyncTask<UUID, String , Person> {
        private String personGroupId;
        public PersonDetectionTask(String personGroupId) {
            this.personGroupId = personGroupId;
        }

        @Override
        protected Person doInBackground(UUID... uuids) {
            try{
                return faceServiceClient.getPerson(personGroupId,uuids[0]);
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
            Toast.makeText(getApplicationContext(),person.name, Toast.LENGTH_SHORT).show();
            //new AddFaceTask().execute(person.personId);
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }
    }

}
