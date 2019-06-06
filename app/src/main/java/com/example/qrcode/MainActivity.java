package com.example.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.qrcode.imageProcessing.QrDetector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OUVERTURE DE LA LIBRAIRIE OPENCV
        System.loadLibrary("opencv_java3");

        setContentView(R.layout.activity_main);
    }

    public void onClick(View view)
    {
        Intent intent = new Intent(this,CameraPreview.class);
        startActivity(intent);
    }

    public void launchDebugMode(View view)
    {
        Intent intent = new Intent(this,DebugMode.class);
        startActivity(intent);
    }

}
