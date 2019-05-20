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

        int[] ids = new int[]{R.drawable._1, R.drawable._2, R.drawable._3, R.drawable._4, R.drawable._5, R.drawable._6, R.drawable._7, R.drawable._8, R.drawable._9, R.drawable._10,
                R.drawable._11, R.drawable._12, R.drawable._13, R.drawable._14, R.drawable._15, R.drawable._16, R.drawable._17, R.drawable._18, R.drawable._19, R.drawable._20,
                R.drawable._21, R.drawable._22, R.drawable._23, R.drawable._24, R.drawable._25, R.drawable._26, R.drawable._27, R.drawable._28, R.drawable._29, R.drawable._30};

        //ids = new int[]{R.drawable.test, R.drawable.test2};

        Drawable d = ContextCompat.getDrawable(this, ids[(int)(Math.random()*ids.length)]);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

        QrDetector detector = new QrDetector(bitmap);

        PhotoColorPicker.photo = detector.getDebugBitmap();
        Intent photo = new Intent(this, PhotoColorPicker.class);
        startActivity(photo);

    }

}
