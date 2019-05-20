package com.example.qrcode.imageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageFilter {

    public static double FILTRE_RATIO_IMAGE = 0.15;//0.27;

    private Bitmap bitmapOrig;
    private Mat matOrig;

    private Mat matBlur;

    private Mat matFiltered;
    private byte[] arrayFiltered;

    private Bitmap bitmapDebug;
    private Mat matDebug;
    private byte[] arrayDebug;

    public ImageFilter(Bitmap bitmapOrig) {

        this.bitmapOrig = bitmapOrig;
        this.matOrig = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC1);
        Utils.bitmapToMat(bitmapOrig, matOrig);
        Imgproc.cvtColor(matOrig, matOrig, Imgproc.COLOR_RGB2GRAY);

        blur();
        filter();

    }

    private void blur() {

        int tailleFiltre = (int)(Math.min(QrDetector.IMAGE_WIDTH, QrDetector.IMAGE_HEIGHT) * FILTRE_RATIO_IMAGE);
        tailleFiltre -= 1 - tailleFiltre % 2;
        matBlur = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC1);
        Imgproc.GaussianBlur(matOrig, matBlur, new org.opencv.core.Size(tailleFiltre, tailleFiltre), 0.3*((tailleFiltre-1)*0.5 - 1) + 0.8);

    }

    private void filter() {

        byte[] buff1 = new byte[QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];
        byte[] buff2 = new byte[QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];
        arrayFiltered = new byte[QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];

        matOrig.get(0, 0, buff1);
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

        arrayDebug = new byte[3 * QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];
        for(int i = 0; i < arrayFiltered.length; i++) {
            arrayDebug[3*i] = copy[i];
            arrayDebug[3*i+1] = copy[i];
            arrayDebug[3*i+2] = copy[i];
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
