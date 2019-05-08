package com.example.qrcode.imageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class QrDetector {


    private final static int IMAGE_WIDTH = 600, IMAGE_HEIGHT = 800;


    public static Bitmap analyze(Bitmap imageBitmap) {

        imageBitmap = imageBitmap.createScaledBitmap(imageBitmap, IMAGE_WIDTH, IMAGE_HEIGHT, false);

        Mat image = ImageFilter.bitmapToMat(imageBitmap, IMAGE_WIDTH, IMAGE_HEIGHT);

        Mat image_high = ImageFilter.filter(image, IMAGE_WIDTH, IMAGE_HEIGHT);

        //Log.d("IMPOROOROTTANTNNTNTNT", image_high.rows() + " ; " + image_high.cols() + " ; " + image_high.width() + " ; " + image_high.height());


        Bitmap b = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(image_high, b);

        return b;

    }


}
