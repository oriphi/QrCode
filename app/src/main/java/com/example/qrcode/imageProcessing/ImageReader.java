package com.example.qrcode.imageProcessing;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageReader {


    private Mat matTransform;
    private int size;

    private int[][] code;


    public ImageReader(Mat matTransform, int size) {

        this.matTransform = matTransform;
        this.size = size;

        analyze();

    }


    private void analyze() {

        Imgproc.GaussianBlur(matTransform, matTransform, new org.opencv.core.Size(Transform.MODULE_SIZE, Transform.MODULE_SIZE), 0.3*((Transform.MODULE_SIZE-1)*0.5 - 1) + 0.8);

        code = new int[size][size];

        int val;
        byte[] buf = new byte[1];

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                matTransform.get(i * Transform.MODULE_SIZE + Transform.MODULE_SIZE/2, j * Transform.MODULE_SIZE + Transform.MODULE_SIZE/2, buf);
                val = buf[0];
                if(val < 0) {
                    val += 256;
                }

                if(val < 128) {
                    code[i][j] = 0;
                } else {
                    code[i][j] = 1;
                }
            }
        }

    }

    public Mat getMatTransform() {
        return matTransform;
    }

    public int[][] getCode() {
        return code;
    }


}
