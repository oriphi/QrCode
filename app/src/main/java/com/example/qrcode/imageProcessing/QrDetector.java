package com.example.qrcode.imageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class QrDetector {


    public static int IMAGE_WIDTH = 600, IMAGE_HEIGHT = 800;

    private Bitmap imageBitmap;
    private Bitmap debugBitmap;

    private int status;
    private int[][] code;

    public QrDetector(Bitmap imageBitmap) {

        this.imageBitmap = imageBitmap;
        IMAGE_WIDTH = imageBitmap.getWidth();
        IMAGE_HEIGHT = imageBitmap.getHeight();

        analyze();

    }


    public void analyze() {

        long t = System.nanoTime();

        ImageFilter filter = new ImageFilter(imageBitmap);
        byte[] array = filter.getArrayFiltered();
        byte[] arrayDebug = filter.getArrayDebug();

        PatternFinder finder = new PatternFinder(array, arrayDebug);

        if(finder.getStatus() == -1) {
            status = -1;
            return;
        }

        Transform transform = new Transform(filter.getMatFiltered(), finder.getBg(), finder.getHg(), finder.getHd(), arrayDebug);

        if(transform.getStatus() == -1) {
            status = -1;
            return;
        }

        ImageReader reader = new ImageReader(transform.getMatTransform(), transform.getSize());

        Mat matTransformBack = transform.transformBack(reader.getMatTransformDebug());
        matTransformBack = filter.add(filter.getMatOrig(), matTransformBack);

        code = reader.getCode();

        //filter.debug();
        //filter.debug(reader.getMatTransform());
        filter.debug(matTransformBack);
        debugBitmap = filter.getBitmapDebug();

        Log.d("TEMPS EXECUTION TOTAL", String.valueOf((System.nanoTime()-t) / 1000000));
    }

    public Bitmap getDebugBitmap() {
        return debugBitmap;
    }

    public int[][] getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }


}
