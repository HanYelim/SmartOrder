package com.example.yelimhan.smartorder.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.Person;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class RegisterFaceActivity extends AppCompatActivity {

    private Button cameraButton;
    private Button addPersonBtn;
    private TextView textView;
    private EditText editPersonName;

    private FaceServiceClient faceServiceClient = new FaceServiceRestClient("https://koreacentral.api.cognitive.microsoft.com/face/v1.0", "873449264b7740e3ba3ca53b3c9df99f");
    private final String personGroupId = "smartordergroup";
    private static int RESULT_CAPTURE_IMAGE = 0;

    public static Bitmap mBitmap;
    Face[] result = null;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==RESULT_CAPTURE_IMAGE && resultCode==RESULT_OK && null!=data)
        {
            mBitmap = (Bitmap) data.getExtras().get("data");
            textView.setText("등록된 사진이 있습니다.");
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
        editPersonName = (EditText) findViewById(R.id.editpersonname);
        textView = (TextView)findViewById(R.id.check_photo);


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RESULT_CAPTURE_IMAGE);
            }
        });


        addPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                new detectTask().execute(inputStream);

                // 사람 추가
                new AddPersonToGroupTask(personGroupId, editPersonName.getText().toString()).execute(inputStream);
                new TrainingTask(personGroupId).execute(personGroupId);
            }
        });
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

    class AddPersonToGroupTask extends AsyncTask<InputStream, String, Face> {

        private String personGroupId;
        private String personName;

        public AddPersonToGroupTask(String personGroupId, String personName) {
            this.personGroupId = personGroupId;
            this.personName = personName;
        }
        @Override
        protected Face doInBackground(InputStream... inputStreams) {
            try {
                faceServiceClient.getPersonGroup(personGroupId);
                CreatePersonResult person = faceServiceClient.createPerson(personGroupId,personName, "");

                Log.d("person id : ", person.personId.toString());
                faceServiceClient.addPersonFace(personGroupId,person.personId,inputStreams[0],"persontest",result[0].faceRectangle); ///

                return null;
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
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            mDialog.dismiss();
            //facesDetected = faces;
            Toast.makeText(getApplicationContext(),"detect finished", Toast.LENGTH_SHORT).show();
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
