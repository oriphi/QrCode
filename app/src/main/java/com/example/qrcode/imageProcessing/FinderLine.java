package com.example.qrcode.imageProcessing;

import java.util.ArrayList;

public class FinderLine {

    private int j1, j2, j3, j4;

    private double score;

    private int direction;
    private int k;
    private int[] kMin, kMax;

    private int iMin, iMax;


    public FinderLine(int j1, int j2, int j3, int j4, int direction, int k, int iMin, int iMax, double score) {

        this.j1 = j1;
        this.j2 = j2;
        this.j3 = j3;
        this.j4 = j4;

        this.score = score;

        this.direction = direction;
        this.k = k;
        this.iMin = iMin;
        this.iMax = iMax;

        kMin = new int[4];
        kMax = new int[4];

        if(direction == 0) {
            kMin[0] = k;
            kMax[0] = k+1;
            kMin[1] = iMin;
            kMax[1] = iMax;
            kMin[2] = QrDetector.IMAGE_HEIGHT - 1 + iMin - k;
            kMax[2] = QrDetector.IMAGE_HEIGHT - 1 + iMax - k;
            kMin[3] = k + iMin;
            kMax[3] = k + iMax;
        }
        else if(direction == 1) {
            kMin[0] = iMin;
            kMax[0] = iMax;
            kMin[1] = k;
            kMax[1] = k+1;
            kMin[2] = QrDetector.IMAGE_HEIGHT - iMax + k;
            kMax[2] = QrDetector.IMAGE_HEIGHT - iMin + k;
            kMin[3] = k + iMin;
            kMax[3] = k + iMax;
        }
        else if(direction == 2) {
            kMin[2] = k;
            kMax[2] = k+1;
            k -= QrDetector.IMAGE_HEIGHT - 1;
            if(k >= 0) {
                kMin[0] = iMin;
                kMax[0] = iMax;
                kMin[1] = iMin + k;
                kMax[1] = iMax + k;
                kMin[3] = k + 2*iMin;
                kMax[3] = k + 2*iMax - 1;
            } else {
                kMin[0] = iMin - k;
                kMax[0] = iMax - k;
                kMin[1] = iMin;
                kMax[1] = iMax;
                kMin[3] = -k + 2*iMin;
                kMax[3] = -k + 2*iMax - 1;
            }

        }
        else {
            kMin[3] = k;
            kMax[3] = k+1;
            k -= QrDetector.IMAGE_WIDTH - 1;
            if(k >= 0) {
                kMin[0] = iMin + k;
                kMax[0] = iMax + k;
                kMin[1] = QrDetector.IMAGE_WIDTH - iMax;
                kMax[1] = QrDetector.IMAGE_WIDTH - iMin;
                kMin[2] = QrDetector.IMAGE_WIDTH + QrDetector.IMAGE_HEIGHT - k - 2*iMax;
                kMax[2] = QrDetector.IMAGE_WIDTH + QrDetector.IMAGE_HEIGHT - k - 2*iMin - 1;
            } else {
                kMin[0] = iMin;
                kMax[0] = iMax;
                kMin[1] = QrDetector.IMAGE_WIDTH + k - iMax;
                kMax[1] = QrDetector.IMAGE_WIDTH + k - iMin;
                kMin[2] = QrDetector.IMAGE_WIDTH + QrDetector.IMAGE_HEIGHT + k - 2*iMax;
                kMax[2] = QrDetector.IMAGE_WIDTH + QrDetector.IMAGE_HEIGHT + k - 2*iMin - 1;
            }

        }

    }

    public double getScore() {
        return score;
    }

    public int getDirection() {
        return direction;
    }

    public int getK() {
        return k;
    }

    public int[] getKMin() {
        return kMin;
    }

    public int[] getKMax() {
        return kMax;
    }

    public int getJ1() {
        return j1;
    }
    public int getJ2() {
        return j2;
    }
    public int getJ3() {
        return j3;
    }
    public int getJ4() {
        return j4;
    }

    public int getIMin() {
        return iMin;
    }
    public int getIMax() {
        return iMax;
    }

    public static boolean intersect(FinderLine l1, FinderLine l2) {

        int k1Min = l1.getKMin()[l2.getDirection()];
        int k1Max = l1.getKMax()[l2.getDirection()];

        int k2Min = l2.getKMin()[l1.getDirection()];
        int k2Max = l2.getKMax()[l1.getDirection()];

        return l2.getK() >= k1Min && l2.getK() < k1Max && l1.getK() >= k2Min && l1.getK() < k2Max;
    }

    public static double score(ArrayList<Integer> borders, int n) {

        int sum = borders.get(n + 5) - borders.get(n);
        double score = 1;

        double v1, v2;
        for(int i = 0; i < 5; i++) {
            v1 = (borders.get(n + i + 1) - borders.get(n + i)) * 1.0 / sum;
            v2 = (i==2)? 3.0/7 : 1.0/7;

            if(v1 > v2) {
                score *= v2 / v1;
            } else {
                score *= v1 / v2;
            }
        }

        return score;
    }

}
