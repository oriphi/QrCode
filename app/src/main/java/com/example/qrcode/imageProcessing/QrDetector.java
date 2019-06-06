package com.example.qrcode.imageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.qrcode.DebugMode;

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

    private String debugMessage = "";

    public QrDetector(Bitmap imageBitmap, boolean resize) {

        if(resize) {

            IMAGE_WIDTH = imageBitmap.getWidth()/3;
            IMAGE_HEIGHT = imageBitmap.getHeight()/3;
            this.imageBitmap = Bitmap.createScaledBitmap(imageBitmap, IMAGE_WIDTH,IMAGE_HEIGHT, false);

        } else {

            IMAGE_WIDTH = imageBitmap.getWidth();
            IMAGE_HEIGHT = imageBitmap.getHeight();
            this.imageBitmap = imageBitmap;

        }

        analyze();

    }


    public void analyze() {

        debugMessage = "";
        long t0 = System.nanoTime();

        // premier filtrage de l'image (passe haut et éventuellement redimmensionnement)
        ImageFilter filter = new ImageFilter(imageBitmap);
        byte[] array = filter.getArrayFiltered();
        byte[] arrayDebug = filter.getArrayDebug();

        long t1 = System.nanoTime();

        // on recherche les finders
        PatternFinder finder = new PatternFinder(array, arrayDebug);

        if(finder.getStatus() == -1) {
            status = -1;
            return;
        }

        long t2 = System.nanoTime();

        // on transforme l'image
        Transform transform = new Transform(filter.getMatFiltered(), finder.getBg(), finder.getHg(), finder.getHd(), arrayDebug);

        if(transform.getStatus() == -1) {
            status = -1;
            return;
        }

        long t3 = System.nanoTime();

        // on lit l'image
        ImageReader reader = new ImageReader(transform.getMatTransform(), transform.getSize());

        Mat matTransformBack = null;
        if(DebugMode.DEBUG_MODE) {
            matTransformBack = transform.transformBack(reader.getMatTransformDebug());
            matTransformBack = filter.add(filter.getMatOrig(), matTransformBack);
        }

        code = reader.getCode();

        long t4 = System.nanoTime();

        if(DebugMode.DEBUG_MODE) {
            //filter.debug();
            //filter.debug(reader.getMatTransform());
            filter.debug(matTransformBack);
            debugBitmap = filter.getBitmapDebug();
        }


        long t5 = System.nanoTime();

        debugMessage = ((t5-t0)/1000000) + "ms";

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

    public String getDebugMessage() {
        return debugMessage;
    }


}
