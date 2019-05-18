package com.example.qrcode.imageProcessing;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.Comparator;

public class Transform {

    public static int MODULE_SIZE = 7;

    private byte[] array;
    private Mat mat;
    private Mat matTf;

    private FinderGroup bg, hg, hd;

    public Transform(byte[] array, Mat mat, FinderGroup bg, FinderGroup hg, FinderGroup hd) {

        this.array = array;
        this.mat = mat;

        this.bg = bg;
        this.hg = hg;
        this.hd = hd;

        analyze();

    }

    public Point[] findBorders(FinderGroup g, final double angle) {

        int N = g.getBorders().length;
        double[][] borders = new double[N][3];
        double c = Math.cos(angle), s = Math.sin(angle);
        for(int n = 0 ; n < N; n++) {
            borders[n][0] = g.getBorders()[n].x;
            borders[n][1] = g.getBorders()[n].y;
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

    private void draw(Point p, byte val) {
        int j = (int)p.y * QrDetector.IMAGE_WIDTH + (int)p.x;

        array[j-2] = val;
        array[j-1] = val;
        array[j] = val;
        array[j+1] = val;
        array[j+2] = val;

        array[j-2*QrDetector.IMAGE_WIDTH] = val;
        array[j-QrDetector.IMAGE_WIDTH] = val;
        array[j+QrDetector.IMAGE_WIDTH] = val;
        array[j+2*QrDetector.IMAGE_WIDTH] = val;

        array[j-QrDetector.IMAGE_WIDTH-1] = val;
        array[j-QrDetector.IMAGE_WIDTH+1] = val;
        array[j+QrDetector.IMAGE_WIDTH-1] = val;
        array[j+QrDetector.IMAGE_WIDTH+1] = val;
    }

    private void analyze() {


        double angleHorz = Math.atan2(hg.getCenter().x - hd.getCenter().x, hg.getCenter().y - hd.getCenter().y);
        double angleVert = Math.atan2(bg.getCenter().x - hg.getCenter().x, bg.getCenter().y - hg.getCenter().y);

        Point[] hgVert = findBorders(hg, angleVert);
        Point[] hgHorz = findBorders(hg, angleHorz);
        Point[] bgVert = findBorders(bg, angleVert);
        Point[] hdHorz = findBorders(hd, angleHorz);

        Point c1 = MathUtil.intersectLines(bgVert[0], hgVert[0], hgHorz[1], hdHorz[1]);
        Point c2 = MathUtil.intersectLines(bgVert[0], hgVert[0], hgHorz[0], hdHorz[0]);
        Point c3 = MathUtil.intersectLines(bgVert[1], hgVert[1], hgHorz[0], hdHorz[0]);
        Point c4 = MathUtil.intersectLines(bgVert[1], hgVert[1], hgHorz[1], hdHorz[1]);

        draw(hgVert[0], (byte) 127);
        draw(hgVert[1], (byte) -128);
        draw(bgVert[0], (byte) 127);
        draw(bgVert[1], (byte) -128);


        draw(hgHorz[0], (byte) 127);
        draw(hgHorz[1], (byte) -128);
        draw(hdHorz[0], (byte) 127);
        draw(hdHorz[1], (byte) -128);


        Mat src_mat = new Mat(4,1, CvType.CV_32FC2);
        Mat dst_mat = new Mat(4,1,CvType.CV_32FC2);
        src_mat.put(0,0, c1.x, c1.y, c2.x, c2.y, c3.x, c3.y, c4.x, c4.y);
        dst_mat.put(0,0, 0.0, 7.0*MODULE_SIZE, 0.0, 0.0, 7.0*MODULE_SIZE, 0.0, 7.0*MODULE_SIZE, 7.0*MODULE_SIZE);

        Mat tf = Imgproc.getPerspectiveTransform(src_mat, dst_mat);

        matTf = new Mat(QrDetector.IMAGE_HEIGHT,QrDetector.IMAGE_WIDTH, CvType.CV_8UC1);
        Imgproc.warpPerspective(mat, matTf, tf, matTf.size());

    }

    public Mat getMatTf() {
        return matTf;
    }

}
