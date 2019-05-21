package com.example.yelimhan.smartorder.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.example.yelimhan.smartorder.R;
import com.example.yelimhan.smartorder.CameraPreview;

public class CameraActivity extends Activity {
    private static final String TAG = "CamTestActivity";
    public CameraPreview preview;
    Camera camera;
    Activity act;
    Context ctx;
    String pictureFile = "";
    Button button_main_capture, button_reg;
    private boolean safeToTakePicture = false;
    boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        act = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        button_main_capture = findViewById(R.id.capture);
        button_reg = findViewById(R.id.button_reg);

        setContentView(R.layout.activity_camera);

        preview = new CameraPreview(this, (SurfaceView)findViewById(R.id.camera_preview_main));
        preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ((RelativeLayout) findViewById(R.id.layout_camera)).addView(preview);
        preview.setKeepScreenOn(true);
        preview.setCamera(camera);

    }

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = Camera.open(1);
                camera.startPreview();
                preview.setCamera(camera);

            } catch (RuntimeException ex){
                Toast.makeText(ctx, "No camera harware found", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());
                pictureFile = outFile.getAbsolutePath();
                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }

            if(safeToTakePicture && flag){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            //intent.putExtra("PATH",pictureFile );
            //MainActivity.this.setResult(RESULT_OK, intent);
            //MainActivity.this.finish();
            return null;
        }

    }

    public void captureClick(View view){
        flag = true;
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //여기에 딜레이 후 시작할 작업들을 입력
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        }, 500);// 0.5초 정도 딜레이를 준 후 시작


        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //여기에 딜레이 후 시작할 작업들을 입력
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                safeToTakePicture = true;
            }
        }, 1000);// 0.5초 정도 딜레이를 준 후 시작

    }

    public void registerClick(View view){

        Intent i = new Intent(getApplicationContext(), RegisterFaceActivity.class);
        startActivity(i);
        finish();
    }
}
