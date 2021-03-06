package com.example.yelimhan.smartorder;


import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    public CameraPreview(Context context, SurfaceView sv) {
        super(context);

        mSurfaceView = sv;
//        addView(mSurfaceView);

        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();

            // get Camera parameters
            Camera.Parameters params = mCamera.getParameters();
            if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                params.set("orientation", "portrait");
                camera.setDisplayOrientation(90);
                params.setRotation(90);
            } else {
                params.set("orientation", "landscape");
                camera.setDisplayOrientation(0);
                params.setRotation(0);
            }
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                // set the focus mode
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                // set Camera parameters
                mCamera.setParameters(params);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        //final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        final int width = 1920;
        final int height = 2560;
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            //int previewWidth = width;
            //int previewHeight = height;
            int previewWidth = 1920;
            int previewHeight = 2560;


            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                int m_resWidth;
                int m_resHeight;
                m_resWidth = mCamera.getParameters().getPictureSize().width;
                m_resHeight = mCamera.getParameters().getPictureSize().height;
                Camera.Parameters parameters = mCamera.getParameters();
        //아래 숫자를 변경하여 자신이 원하는 해상도로 변경한다

                Log.d("width : ", String.valueOf(m_resWidth));
                Log.d("height : ", String.valueOf(m_resHeight));

                //m_resWidth = 480;
                //m_resHeight = 640;


                List<Size> allSizes = parameters.getSupportedPictureSizes();
                Camera.Size size = allSizes.get(0); // get top size
                for (int i = 0; i < allSizes.size(); i++) {
                        if(allSizes.get(i).width == 640){
                            size = allSizes.get(i);
                            break;
                        }
                }
                parameters.setPictureSize(size.width, size.height);

                Log.d("width : ", String.valueOf(size.width));
                Log.d("height : ", String.valueOf(size.height));

                mCamera.setParameters(parameters);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//        if(mCamera != null) {
//            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//            requestLayout();
//
//            mCamera.setParameters(parameters);
//            mCamera.startPreview();
//        }

        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                int m_resWidth;
                int m_resHeight;
                m_resWidth = mCamera.getParameters().getPictureSize().width;
                m_resHeight = mCamera.getParameters().getPictureSize().height;
                Camera.Parameters parameters = mCamera.getParameters();
                //아래 숫자를 변경하여 자신이 원하는 해상도로 변경한다

                Log.d("width : ", String.valueOf(m_resWidth));
                Log.d("height : ", String.valueOf(m_resHeight));

                //m_resWidth = 480;
                //m_resHeight = 640;


                List<Size> allSizes = parameters.getSupportedPictureSizes();
                Camera.Size size = allSizes.get(0); // get top size
                for (int i = 0; i < allSizes.size(); i++) {
                    if(allSizes.get(i).width == 640){
                        size = allSizes.get(i);
                        break;
                    }
                }
                parameters.setPictureSize(size.width, size.height);

                Log.d("width : ", String.valueOf(size.width));
                Log.d("height : ", String.valueOf(size.height));

                requestLayout();

                mCamera.setParameters(parameters);
                mCamera.startPreview();
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }


}
