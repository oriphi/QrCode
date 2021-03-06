

// Code trouvé sur : https://android.jlelse.eu/the-least-you-can-do-with-camera2-api-2971c8c81b8b
package com.example.qrcode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import com.example.qrcode.decodage.QrFactory;
import com.example.qrcode.decodage.QrRead;
import com.example.qrcode.imageProcessing.QrDetector;

import java.util.Collections;

public class CameraPreview extends AppCompatActivity {

    int CAMERA_REQUEST_CODE = 100; // Code spécifique à chaque application (la valeur n'importe peu)
    private CameraManager cameraManager; // Objet s'occupant de gérer toute les caméras de l'appareil
    private CameraDevice cameraDevice;  // Caméra utilisée
    private CameraCaptureSession cameraCaptureSession; // Objet nécessaire à la prise de photo ou la visualisation du feed
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest captureRequest;      // Demande de prise de photo
    private int cameraFacing;           // Indice de la caméra que l'on va utiliser dans la liste des caméras
    private CameraDevice.StateCallback stateCallback;       // Fonction de callback pour la création de caméra


    private Size previewSize;                   // Taille des photos capturé par la caméra
    private String cameraId;                    // Identifiant unique de la caméra


    private HandlerThread backgroundThread;     // Création d'un thread pour gérer la caméra en arrière plan
    private Handler backgroundHandler;

    private TextureView textureView;            // Objet permettant d'afficher le feed de la caméra
    private TextureView.SurfaceTextureListener surfaceTextureListener;    // Permet "d'écouter" l'objet textureView, dès qu'il se passe quelque chose
                                                                        // sur textureView, on execute cette fonction
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Activité qui visualise le feed de la caméra et qui permet de prendre une photo
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        // On demande aussi la permission d'écrire dans le stockage de l'appareil (dépend des fonctionnalités qu'on voudra implémenter

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);

        // Récupération du CameraManager, c'est lui qui va nous donner l'acces à la caméra
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;          // On selectionne la caméra dorsale
        textureView = (TextureView)  findViewById( R.id.textureView );  // Selection du bon objet TextureView
        surfaceTextureListener = new TextureView.SurfaceTextureListener(){
            // Dès que l'objet est prêt, il va executer setUpCamera et openCamera, afin de commencer à afficher le feed video

            // On implémente les autres fonctions car il s'agit d'une interface, mais elle ne font rien
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

        // Création d'un Callback pour la caméra, qui va monitorer l'état de la caméra (si il y a une erreur,
        // si elle se ferme, si elle s'ouvre ...
        stateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                // Quand on ouvre la caméra, on fait une requête de Capture Session
                CameraPreview.this.cameraDevice = camera;
                createPreviewSession();
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                // si elle se déconnecte, on ferme la caméra
                cameraDevice.close();
                CameraPreview.this.cameraDevice = null;
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                // On pourrait gérer les erreurs, mais ce n'est pas trop important

            }
        };


        // Configuration du bouton de prise de photo
        FloatingActionButton button = findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //finalImage = image.createScaledBitmap(image, 600,800, false);

                // Lancement de la détection de qr code
                QrDetector detector = new QrDetector(textureView.getBitmap(), true);
                if(detector.getStatus() == -1) {
                    detector = new QrDetector(textureView.getBitmap(), false);
                }

                // on vérifie si un qr code a été détecté
                if(detector.getStatus() == -1) {

                    // pas de qr code détecté
                    Context context = getApplicationContext();
                    CharSequence text = "Erreur : Aucun QR Code n'a été détecté !";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                } else {

                    // Un QR Code a été détecté : Lancement du décodage
                    int[][] code = detector.getCode();
                    QrFactory fact = new QrFactory();
                    String results;
                    try {
                        QrRead read = fact.getQrType(code); // Création du type de QrCode selon la taille
                        try {
                          results = read.getQrMessageDecode();
                        }
                        catch (Exception e) {   // Exception sur get QrMessageDecode()
                          results = "Erreur : " + e.getMessage();
                        }
                    }
                    catch (Exception e) {       // Exception sur getQrType()
                        results = "Erreur : " + e.getMessage();
                    }

                    // Si on est en mode débug, on affiche le débug
                    if(DebugMode.DEBUG_MODE) {
                        PhotoColorPicker.photo = detector.getDebugBitmap();
                        Intent photo = new Intent(CameraPreview.this, PhotoColorPicker.class);
                        startActivity(photo);
                    }

                    AlertDialog alert = new AlertDialog(results);
                    alert.show(getSupportFragmentManager(),"Alert Dialog");

                    Context context = getApplicationContext();
                    CharSequence text = detector.getDebugMessage();
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }

            }
        });

    }

    private void setUpCamera()
            // Fonction utilisée pour configurer la caméra une fois que le preview est disponible
    {
        try {
            for (String cameraId : cameraManager.getCameraIdList())
            {
                // On recherche la caméra parmis toutes celle disponible
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == cameraFacing)
                {
                    // Si c'est la caméra dorsale, on regarde la resolution de sortie et on selectionne la première (certainement la plus grande, à vérifier)
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                    this.cameraId = cameraId; // On a le bon identifiant pour notre caméra, on le garde pour pouvoir ensuite
                    // la retrouver
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    // Fonction qui essaye de récuperer la caméra grâce à son id, en lui donnant un callback et un thread pour s'executer
    private void openCamera()
    {
        try
        {
            // Il faut d'abbord checker si on a bien les permissions pour acceder à la caméra
            if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
            }

        } catch(CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
    // Crée un thread en arrière plan dédié à la gestion de la caméra
    private void openBackgroundThread()
    {
        backgroundThread = new HandlerThread("Camera_backgrouned_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    @Override
    // Gère les threads de l'application lorqu'on met l'appli en arrière plan ou qu'on la feerme
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


    // Termine la CaptureSession
    private void closeCamera()
    {
        if(cameraCaptureSession != null)
        {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    // Ferme le thread en Arrière-plan
    private void closeBackgroundThread()
    {
        if(backgroundHandler != null)
        {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    // Création de la capture session, qui va nous permettre de prendre des photo et d'avoir accès au
    // flux video
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

}
