package com.example.qrcode.imageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.qrcode.DebugMode;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageFilter {

    public static double FILTRE_RATIO_IMAGE = 0.15;//0.27;

    private Bitmap bitmapOrig;
    private Mat matOrig;
    private Mat matOrigGray;

    private Mat matBlur;

    private Mat matFiltered;
    private byte[] arrayFiltered;

    private Bitmap bitmapDebug;
    private Mat matDebug;
    private byte[] arrayDebug;

    public ImageFilter(Bitmap bitmapOrig) {

        this.bitmapOrig = bitmapOrig;
        this.matOrig = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC4);
        Utils.bitmapToMat(bitmapOrig, matOrig);
        //Log.d("TYPE MAT ORIG", matOrig.type()+" "+CvType.CV_8UC3+" "+CvType.CV_8UC4);
        this.matOrigGray = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC1);
        Imgproc.cvtColor(matOrig, matOrigGray, Imgproc.COLOR_RGB2GRAY);

        blur();
        filter();

    }

    private void blur() {

        int tailleFiltre = (int)(Math.min(QrDetector.IMAGE_WIDTH, QrDetector.IMAGE_HEIGHT) * FILTRE_RATIO_IMAGE);
        tailleFiltre -= 1 - tailleFiltre % 2;
        matBlur = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC1);
        Imgproc.GaussianBlur(matOrigGray, matBlur, new org.opencv.core.Size(tailleFiltre, tailleFiltre), 0.3*((tailleFiltre-1)*0.5 - 1) + 0.8);

    }

    private void filter() {

        byte[] buff1 = new byte[QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];
        byte[] buff2 = new byte[QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];
        arrayFiltered = new byte[QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];

        matOrigGray.get(0, 0, buff1);
        matBlur.get(0, 0, buff2);

        int x;

        for(int i = 0; i < QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH; i++) {
            if(buff1[i] < 0) {
                x = buff1[i] + 256;
            } else {
                x = buff1[i];
            }
            if(buff2[i] < 0) {
                x -= buff2[i] + 256;
            } else {
                x -= buff2[i];
            }
            if(x < -128) {
                arrayFiltered[i] = -128;
            } else if(x > 127) {
                arrayFiltered[i] = 127;
            } else {
                arrayFiltered[i] = (byte)x;
            }
        }

        // arrayFiltered : entre -128 et 127
        // on doit rajouter 128 à chaque élément pour que l'image affichée soit correcte
        byte[] copy = arrayFiltered.clone();
        for(int i = 0; i < QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH; i++) {
            copy[i] += 128;
        }
        matFiltered = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC1);
        matFiltered.put(0, 0, copy);


        if(DebugMode.DEBUG_MODE) {

            arrayDebug = new byte[3 * QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];
            for(int i = 0; i < arrayFiltered.length; i++) {
                arrayDebug[3*i] = copy[i];
                arrayDebug[3*i+1] = copy[i];
                arrayDebug[3*i+2] = copy[i];
            }

        }

    }

    // une fois le tableau arrayDebug modifié, cette méthode crée les objets Mat et Bitmap correspondants
    public void debug() {

        matDebug = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC3);
        matDebug.put(0, 0, arrayDebug);

        bitmapDebug = Bitmap.createBitmap(QrDetector.IMAGE_WIDTH, QrDetector.IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matDebug, bitmapDebug);

    }

    public void debug(Mat matTransform) {

        matDebug = matTransform;
        bitmapDebug = Bitmap.createBitmap(matDebug.width(), matDebug.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matDebug, bitmapDebug);

    }

    public Mat add(Mat m1, Mat m2) {
        Mat result = new Mat(m1.rows(), m1.cols(), CvType.CV_8UC4);

        byte[] buf1 = new byte[m1.rows()*m1.cols()*4];
        byte[] buf2 = new byte[buf1.length];
        m1.get(0,0, buf1);
        m2.get(0,0, buf2);

        for(int n = 0; n < buf1.length; n+=4) {
            if(buf2[n+3] != 0) {
                buf1[n] = buf2[n];
                buf1[n+1] = buf2[n+1];
                buf1[n+2] = buf2[n+2];
            }
        }
        result.put(0, 0, buf1);

        //Log.d("RESULT", result.width()+" "+result.height()+" "+result.type());
        //Log.d("M1", m1.width()+" "+m1.height()+" "+m1.type());
        //Log.d("M2", m2.width()+" "+m2.height()+" "+m2.type());

        return result;
    }

    public Bitmap getBitmapOrig() {
        return bitmapOrig;
    }

    public Mat getMatOrig() {
        return matOrig;
    }

    public Mat getMatBlur() {
        return matBlur;
    }

    public Mat getMatFiltered() {
        return matFiltered;
    }

    public byte[] getArrayFiltered() {
        return arrayFiltered;
    }

    public Bitmap getBitmapDebug() {
        return bitmapDebug;
    }

    public Mat getMatDebug() {
        return matDebug;
    }

    public byte[] getArrayDebug() {
        return arrayDebug;
    }
}
