

// Code trouvé sur : https://android.jlelse.eu/the-least-you-can-do-with-camera2-api-2971c8c81b8b
package com.example.qrcode;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.camera2.*;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Collections;

public class CameraPreview extends AppCompatActivity {
    int CAMERA_REQUEST_CODE = 100;
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private int cameraFacing;

    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private Size previewSize;
    private String cameraId;


    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private CameraDevice.StateCallback stateCallback;

    private TextureView textureView;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest captureRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        // On demande aussi la permission d'écrire dans le stockage de l'appareil (dépend des fonctionnalités qu'on voudra implémenter

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;
        textureView = (TextureView)  findViewById( R.id.textureView );
        surfaceTextureListener = new TextureView.SurfaceTextureListener(){
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                setUpCamera();
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        };

        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                CameraPreview.this.cameraDevice = camera;
                createPreviewSession();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                cameraDevice.close();
                CameraPreview.this.cameraDevice = null;
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {

            }
        };


        // Configuration du bouton
        FloatingActionButton button = findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = textureView.getBitmap();
                image = image.createScaledBitmap(image, 600,800, false);
                int width = image.getWidth();
                int height = image.getHeight();
                Log.d("Camera Preview","Hauteur: " + Integer.toString(height));
                Log.d("Camera Preview","Largeur: " + Integer.toString(width));
                Log.d("Camera Preview","Premier Pixel: " + Integer.toString(image.getPixel(0,0)));

                int[] rgb = ColorToRGB(image.getPixel(0,0));
                Log.d("Camera Preview", "[a,r,g,b]" + Arrays.toString(rgb));


            }
        });

    }
    private void setUpCamera()
    {
        try {
            for (String cameraId : cameraManager.getCameraIdList())
            {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing)
                {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                    this.cameraId = cameraId;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera()
    {
        try
        {
            if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
            }

        } catch(CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
    private void openBackgroundThread()
    {
        backgroundThread = new HandlerThread("Camera_backgrouned_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        openBackgroundThread();
        if(textureView.isAvailable())
        {
            setUpCamera();
            openCamera();
        }
        else
        {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        closeCamera();
        closeBackgroundThread();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
        closeBackgroundThread();
    }

    private void closeCamera()
    {
        if(cameraCaptureSession != null)
        {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    private void closeBackgroundThread()
    {
        if(backgroundHandler != null)
        {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    private void createPreviewSession()
    {
        try{
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(previewSize.getWidth(),previewSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);
            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if(cameraDevice == null)
                        return;
                    try
                    {
                        captureRequest = captureRequestBuilder.build();
                        CameraPreview.this.cameraCaptureSession = session;
                        CameraPreview.this.cameraCaptureSession.setRepeatingRequest(captureRequest, null, backgroundHandler);
                    } catch(CameraAccessException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            },backgroundHandler);
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }

    }

    private int[] ColorToRGB(int color)
    {
        // Renvoie les 4 composantes du pixel
        int a,r,g,b;
        a = (color >> 24) & 0xff;
        r = (color >> 16) & 0xff;
        g = (color >> 8) & 0xff;
        b = (color ) & 0xff;

        return new int[]{a,r,g,b};

    }
}
