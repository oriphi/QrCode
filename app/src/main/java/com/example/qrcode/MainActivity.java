package com.example.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.qrcode.imageProcessing.QrDetector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.loadLibrary("opencv_java3"); // OUVERTURE DE LA LIBRAIRIE OPENCV

        setContentView(R.layout.activity_main);
    }


    public void onClick(View view)
    {
        Intent intent = new Intent(this,CameraPreview.class);
        startActivity(intent);
    }

    public void launchAlert(View view){
        AlertDialog alert = new AlertDialog();
        alert.show(getSupportFragmentManager(),"Alert Dialog");
    }

    public void detect(View view) {

        Drawable d = ContextCompat.getDrawable(this, R.drawable._9);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

        QrDetector.IMAGE_WIDTH = bitmap.getWidth();
        QrDetector.IMAGE_HEIGHT = bitmap.getHeight();
        QrDetector detector = new QrDetector(bitmap);

        PhotoColorPicker.photo = detector.getDebugBitmap();
        Intent photo = new Intent(this, PhotoColorPicker.class);
        startActivity(photo);

    }

}
