package com.example.qrcode.imageProcessing;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Transform {

    public static int MODULE_SIZE = 9;

    private Mat mat;

    private Mat tf;
    private Mat matTransform;

    private FinderGroup bg, hg, hd;

    private int size;

    private byte[] arrayDebug;


    public Transform(Mat mat, FinderGroup bg, FinderGroup hg, FinderGroup hd, byte[] arrayDebug) {

        this.mat = mat;
        this.bg = bg;
        this.hg = hg;
        this.hd = hd;
        this.arrayDebug = arrayDebug;

        analyze();

    }

    private Point[] transformPoints(Point[] points, Mat tf) {

        int N = points.length;
        Mat src = new Mat(N,1, CvType.CV_32FC2);
        float[] srcBuf = new float[N * 2];
        for(int n = 0; n < N; n++) {
            srcBuf[2*n] = (float)points[n].x;
            srcBuf[2*n+1] = (float)points[n].y;
        }
        src.put(0,0, srcBuf);

        Mat dst = new Mat(2,1,CvType.CV_32FC2);
        Core.perspectiveTransform(src, dst, tf);

        float[] dstBuf = new float[N * 2];
        dst.get(0, 0, dstBuf);

        points = points.clone();
        for(int n = 0; n < N; n++) {
            points[n] = new Point(dstBuf[2*n], dstBuf[2*n+1]);
        }

        return points;
    }


    public Point[] findBorders(Point[] points, final double angle) {

        int N = points.length;
        double[][] borders = new double[N][3];
        double c = Math.cos(angle), s = Math.sin(angle);
        for(int n = 0 ; n < N; n++) {
            borders[n][0] = points[n].x;
            borders[n][1] = points[n].y;
            borders[n][2] = borders[n][0] * c - borders[n][1] * s;
        }

        Arrays.sort(borders, new Comparator<double[]>() {
            @Override
            public int compare(double[] a, double[] b) {
                return Double.compare(a[2], b[2]);
            }
        });

        int length = borders.length / 16 + 2;
        int nMin = 0;
        double delta = borders[length][2] - borders[0][2];
        while(borders[length+nMin+1][2] - borders[nMin+1][2] < delta) {
            nMin += 1;
            delta = borders[length+nMin][2] - borders[nMin][2];
        }

        int nMax = N - length - 1;
        delta = borders[length + nMax][2] - borders[nMax][2];
        while(borders[length+nMax-1][2] - borders[nMax-1][2] < delta) {
            nMax -= 1;
            delta = borders[length+nMax][2] - borders[nMax][2];
        }

        return new Point[]{new Point(borders[nMin+length/2][0], borders[nMin+length/2][1]),
                new Point(borders[nMax+length/2][0], borders[nMax+length/2][1])};
    }

    private void draw(Point p, byte v1, byte v2, byte v3) {
        int j = (int)p.y * QrDetector.IMAGE_WIDTH + (int)p.x;

        for(int a = -2; a <= 2; a++) {
            for(int b = Math.abs(a)-2; b <= 2-Math.abs(a); b++) {
                arrayDebug[3*(j-a-b*QrDetector.IMAGE_WIDTH)] = v1;
                arrayDebug[3*(j-a-b*QrDetector.IMAGE_WIDTH)+1] = v2;
                arrayDebug[3*(j-a-b*QrDetector.IMAGE_WIDTH)+2] = v3;
            }
        }
    }

    private Mat kernel(int width, int height, int... vals) {
        Mat kernel = new Mat(width*MODULE_SIZE, height*MODULE_SIZE, CvType.CV_32F);
        float[] buf = new float[9*MODULE_SIZE*MODULE_SIZE];
        int count = 0;
        int i;
        for(int x = 0; x < width*MODULE_SIZE; x++) {
            for(int y = 0; y < height*MODULE_SIZE; y++) {
                i = (x / MODULE_SIZE) + (y / MODULE_SIZE) * width;
                buf[count++] = vals[i];
            }
        }
        kernel.put(0, 0, buf);
        return kernel;
    }

    private Mat cropTransform(int xStart, int yStart, int width, int height) {
        Rect rectCrop = new Rect(xStart, yStart, width, height);
        Mat matCrop = new Mat(matTransform, rectCrop);
        matCrop.convertTo(matCrop, CvType.CV_32F);
        return matCrop;
    }

    private void findLittleFinder() {

        Mat kernel = kernel(3, 3,
                1, 1, 1,
                1, -1, 1,
                1, 1, 1);

        int width = 7*MODULE_SIZE;
        float xFinder = MODULE_SIZE * (size - 6.5f);
        int xStart = (int)Math.round(xFinder) - width / 2;
        Mat matCrop = cropTransform(xStart, xStart, width, width);

        Mat matFilter = new Mat();
        Imgproc.filter2D(matCrop, matFilter, CvType.CV_32F, kernel);

        float[] buf = new float[width * width];
        matFilter.get(0, 0, buf);
        //float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        int iMax = 0;

        for(int i = 0; i < buf.length; i++) {
            /*if(buf[i] < min) {
                min = buf[i];
            }*/
            if(buf[i] > max) {
                //Log.d("LITTLE FINDER", (i%width) + " ; " + (i/width) + " : " + buf[i]);
                max = buf[i];
                iMax = i;
            }
        }
/*
        for(int i = 0; i < buf.length; i++) {
            buf[i] = 255 * (buf[i] - min) / (max - min);
        }
*/
        //matFilter.put(0, 0, buf);
        //matFilter.convertTo(matFilter, CvType.CV_8UC1);
        //matTransform = matFilter;


        int xMax = iMax % width;
        int yMax = iMax / width;

        Log.d("LITTLE FINDER", xMax + " ; " + yMax);
        Log.d("LITTLE FINDER", (xMax + xStart) + " ; " + (yMax + xStart));


        Mat src_mat = new Mat(4,1, CvType.CV_32FC2);
        Mat dst_mat = new Mat(4,1,CvType.CV_32FC2);

        src_mat.put(0,0, 0.0, MODULE_SIZE*size, 0.0, 0.0, MODULE_SIZE*size, 0.0, xMax + xStart, yMax + xStart);
        dst_mat.put(0,0, 0.0, MODULE_SIZE*size, 0.0, 0.0, MODULE_SIZE*size, 0.0, xFinder, xFinder);

        Mat tf2 = Imgproc.getPerspectiveTransform(src_mat, dst_mat);

        Core.gemm(tf2, tf, 1, new Mat(), 0, tf);


    }

    /*
    // TEST
    private void filter2() {

        Mat kernel = kernel(1, 3, -1, 1, -1);

        float xFinder = MODULE_SIZE * (size + 6.5f);
        float yFinder = MODULE_SIZE * (size / 2f);

        int width = 2*MODULE_SIZE;
        int height = MODULE_SIZE * (size - 10);

        int xStart = (int)Math.round(xFinder) - width / 2;
        int yStart = (int)Math.round(yFinder) - height / 2;

        Mat matCrop = cropTransform(xStart, yStart, width, height);

        Mat matFilter = new Mat();
        Imgproc.filter2D(matCrop, matFilter, CvType.CV_32F, kernel);

        float[] buf = new float[width * height];
        matFilter.get(0, 0, buf);
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        int iMax = 0;

        for(int i = 0; i < buf.length; i++) {
            if(buf[i] < min) {
                min = buf[i];
            }
            if(buf[i] > max) {
                //Log.d("LITTLE FINDER", (i%width) + " ; " + (i/width) + " : " + buf[i]);
                max = buf[i];
                iMax = i;
            }
        }

        for(int i = 0; i < buf.length; i++) {
            buf[i] = 255 * (buf[i] - min) / (max - min);
        }

        matFilter.put(0, 0, buf);
        matFilter.convertTo(matFilter, CvType.CV_8UC1);
        matTransform = matFilter;

    }
    */


    private void analyze() {

        double angleHorz = Math.atan2(hg.getCenter().x - hd.getCenter().x, hg.getCenter().y - hd.getCenter().y);
        double angleVert = Math.atan2(bg.getCenter().x - hg.getCenter().x, bg.getCenter().y - hg.getCenter().y);

        Point[] hgVert = findBorders(hg.getBorders(), angleVert);
        Point[] hgHorz = findBorders(hg.getBorders(), angleHorz);
        Point[] bgVert = findBorders(bg.getBorders(), angleVert);
        Point[] hdHorz = findBorders(hd.getBorders(), angleHorz);

        Point c1 = MathUtil.intersectLines(bgVert[0], hgVert[0], hgHorz[1], hdHorz[1]);
        Point c2 = MathUtil.intersectLines(bgVert[0], hgVert[0], hgHorz[0], hdHorz[0]);
        Point c3 = MathUtil.intersectLines(bgVert[1], hgVert[1], hgHorz[0], hdHorz[0]);
        Point c4 = MathUtil.intersectLines(bgVert[1], hgVert[1], hgHorz[1], hdHorz[1]);

        // DEBUG
        draw(hgVert[0], (byte) 0, (byte) 255, (byte) 0);
        draw(hgVert[1], (byte) 0, (byte) 255, (byte) 0);
        draw(bgVert[0], (byte) 255, (byte) 0, (byte) 0);
        draw(bgVert[1], (byte) 255, (byte) 0, (byte) 0);

        draw(hgHorz[0], (byte) 0, (byte) 255, (byte) 0);
        draw(hgHorz[1], (byte) 0, (byte) 255, (byte) 0);
        draw(hdHorz[0], (byte) 0, (byte) 0, (byte) 255);
        draw(hdHorz[1], (byte) 0, (byte) 0, (byte) 255);

        // On effectue la première transformation
        Mat src_mat = new Mat(4,1, CvType.CV_32FC2);
        Mat dst_mat = new Mat(4,1,CvType.CV_32FC2);

        src_mat.put(0,0, c1.x, c1.y, c2.x, c2.y, c3.x, c3.y, c4.x, c4.y);
        dst_mat.put(0,0, 0.0, 7.0*MODULE_SIZE, 0.0, 0.0, 7.0*MODULE_SIZE, 0.0, 7.0*MODULE_SIZE, 7.0*MODULE_SIZE);

        tf = Imgproc.getPerspectiveTransform(src_mat, dst_mat);


        // Calcul de la taille du qr code
        Point[] points = transformPoints(new Point[] {bg.getCenter(), hd.getCenter()}, tf);
        float sizeF = (float)((points[0].y / MODULE_SIZE + 3.5) + (points[1].x / MODULE_SIZE + 3.5)) / 2;
        size = 21 + 4 * (int)Math.round((sizeF - 21) / 4);
        Log.d("TAILLE DU QR CODE", String.valueOf(size));


        // On repère les points tout en bas, et tout à droite
        Point[] bgBordersTransformed = transformPoints(bg.getBorders(), tf);
        Point[] hdBordersTransformed = transformPoints(hd.getBorders(), tf);

        Point[] bgHorz = findBorders(bgBordersTransformed, -Math.PI/2);
        Point[] hdVert = findBorders(hdBordersTransformed, 0);


        // On remet à l'échelle pour que ça touche les bords de l'image
        Mat scaleMatrix = new Mat(3, 3, tf.type());
        scaleMatrix.put(0, 0, MODULE_SIZE*size / hdVert[1].x, 0, 0, 0, MODULE_SIZE*size / bgHorz[1].y, 0, 0, 0, 1);
        Core.gemm(scaleMatrix, tf, 1, new Mat(), 0, tf);


        matTransform = new Mat(MODULE_SIZE * size, MODULE_SIZE * size, CvType.CV_8UC1);
        Imgproc.warpPerspective(mat, matTransform, tf, matTransform.size());

        if(size > 21) {
            findLittleFinder();
            Imgproc.warpPerspective(mat, matTransform, tf, matTransform.size());
        }

    }

    public Mat getMatTransform() {
        return matTransform;
    }

    public int getSize() {
        return size;
    }
}
