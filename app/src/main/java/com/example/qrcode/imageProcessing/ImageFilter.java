package com.example.qrcode.imageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageFilter {

    public static double FILTRE_RATIO_IMAGE = 0.27;


    public static Mat bitmapToMat(Bitmap bitmap, int width, int height) {

        Mat image = new Mat (width, height, CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, image);
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);

        return image;

    }


    public static Mat filter(Mat image, int width, int height) {

        int tailleFiltre = (int)(Math.min(width, height) * FILTRE_RATIO_IMAGE);
        tailleFiltre -= 1 - tailleFiltre % 2;

        Mat blur = new Mat (height, width, CvType.CV_8UC1);
        Mat result = new Mat (height, width, CvType.CV_8UC1);

        Imgproc.blur(image, blur, new org.opencv.core.Size(tailleFiltre, tailleFiltre));


        byte[] buff1 = new byte[width * height];
        byte[] buff2 = new byte[width * height];
        byte[] buff3 = new byte[width * height];

        image.get(0, 0, buff1);
        blur.get(0, 0, buff2);

        int x;

        for(int i = 0; i < width * height; i++) {

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

            //x += 128;

            if(x < -128) {
                buff3[i] = -128;
            } else if(x > 127) {
                buff3[i] = 127;
            } else {
                buff3[i] = (byte)x;
            }

            //buff3[i] += 128;


        }

        //image.put(0, 0, buff1);
        result.put(0, 0, buff3);

        Core.MinMaxLocResult minMaxLoc = Core.minMaxLoc(blur);
        Log.d("MIN MAX", minMaxLoc.minVal + " ; " + minMaxLoc.maxVal);


        minMaxLoc = Core.minMaxLoc(result);
        Log.d("MIN MAX", minMaxLoc.minVal + " ; " + minMaxLoc.maxVal);


        return result;

    }


}
