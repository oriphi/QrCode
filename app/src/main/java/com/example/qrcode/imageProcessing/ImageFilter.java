package com.example.qrcode.imageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageFilter {

    public static double FILTRE_RATIO_IMAGE = 0.25;//0.27;

    private Bitmap bitmap;
    private Mat mat;
    private byte[] arrayFiltered;
    private Mat matFiltered;

    public ImageFilter(Bitmap bitmap) {

        this.bitmap = bitmap;

        bitmapToMat();
        matToArrayFilter();

    }


    public void bitmapToMat() {

        mat = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

    }


    public void matToArrayFilter() {

        int tailleFiltre = (int)(Math.min(QrDetector.IMAGE_WIDTH, QrDetector.IMAGE_HEIGHT) * FILTRE_RATIO_IMAGE);
        tailleFiltre -= 1 - tailleFiltre % 2;

        Mat blur = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC1);
        matFiltered = new Mat (QrDetector.IMAGE_HEIGHT, QrDetector.IMAGE_WIDTH, CvType.CV_8UC1);

        Imgproc.blur(mat, blur, new org.opencv.core.Size(tailleFiltre, tailleFiltre));


        byte[] buff1 = new byte[QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];
        byte[] buff2 = new byte[QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];
        arrayFiltered = new byte[QrDetector.IMAGE_HEIGHT * QrDetector.IMAGE_WIDTH];

        mat.get(0, 0, buff1);
        blur.get(0, 0, buff2);

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

            //buff3[i] += 128;


        }

        matFiltered.put(0, 0, arrayFiltered);

    }

    public Mat getMat() {
        return mat;
    }

    public byte[] getArrayFiltered(){
        return arrayFiltered;
    }

    public Mat getMatFiltered() {
        return matFiltered;
    }


}
