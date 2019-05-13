package com.example.qrcode.imageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class QrDetector {


    public final static int IMAGE_WIDTH = 600, IMAGE_HEIGHT = 800;

    private Bitmap imageBitmap;
    private Bitmap debugBitmap;

    public QrDetector(Bitmap imageBitmap) {

        this.imageBitmap = imageBitmap.createScaledBitmap(imageBitmap, IMAGE_WIDTH, IMAGE_HEIGHT, false);
        this.debugBitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);

        analyze();

    }


    public void analyze() {

        long t = System.nanoTime();

        ImageFilter filter = new ImageFilter(imageBitmap);
        byte[] array = filter.getArrayFiltered();

        // DEBUG
        Utils.matToBitmap(filter.getMatFiltered(), debugBitmap);

        PatternFinder finder = new PatternFinder(array);

        for(int j = 0; j < array.length; j++) {
            array[j] += 128;
        }

        Mat debugMat = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC1);
        debugMat.put(0, 0, array);
        Utils.matToBitmap(debugMat, debugBitmap);

        Log.d("TEMPS EXECUTION TOTAL", String.valueOf((System.nanoTime()-t) / 1000000));
    }

    public Bitmap getDebugBitmap() {
        return debugBitmap;
    }


}
