package com.example.qrcode.imageProcessing;

import android.util.Log;

import com.example.qrcode.DebugMode;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageReader {


    private Mat matTransform;

    private Mat matTransformDebug;
    private int size;

    private int[][] code;

    public static int MODULE_SIZE_DEBUG = 10;

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
                    code[i][j] = 1;
                } else {
                    code[i][j] = 0;
                }
            }
        }

        if(DebugMode.DEBUG_MODE) {

            int newWidth = MODULE_SIZE_DEBUG*size;
            buf = new byte[newWidth*newWidth*4];
            int i, j;
            for(int n = 0; n < buf.length; n+=4) {

                i = (n/4 / newWidth) / MODULE_SIZE_DEBUG;
                j = (n/4 % newWidth) / MODULE_SIZE_DEBUG;
                if(code[i][j] == 0) {
                    buf[n] = (byte)(255);
                    buf[n+1] = (byte)(255);
                    buf[n+2] = (byte)(255);
                    buf[n+3] = (byte)(255);
                } else if(code[i][j] == 1){
                    buf[n] = 0;
                    buf[n+1] = 0;
                    buf[n+2] = 0;
                    buf[n+3] = (byte)(255);
                } else {
                    buf[n] = 127;
                    buf[n+1] = 127;
                    buf[n+2] = 127;
                    buf[n+3] = (byte)(255);
                }
            }

            matTransformDebug = new Mat(newWidth, newWidth, CvType.CV_8UC4);
            matTransformDebug.put(0, 0, buf);

        }

    }

    public Mat getMatTransform() {
        return matTransform;
    }

    public Mat getMatTransformDebug() {
        return matTransformDebug;
    }

    public int[][] getCode() {
        return code;
    }


}
