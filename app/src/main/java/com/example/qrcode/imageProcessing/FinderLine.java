package com.example.qrcode.imageProcessing;

public class FinderLine {

    private int x1, x2, x3, x4;
    private int y1, y2, y3, y4;

    private double score;

    private int direction;
    private int k;
    private int[] kMin, kMax;

    public FinderLine(int[] x, int[] y, int direction, int k, int iMin, int iMax) {

        this.x1 = x[0];
        this.x2 = x[2];
        this.x3 = x[3];
        this.x4 = x[5];
        this.y1 = y[0];
        this.y2 = y[2];
        this.y3 = y[3];
        this.y4 = y[5];

        this.direction = direction;
        this.k = k;

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

    public static boolean intersect(FinderLine l1, FinderLine l2) {

        int k1Min = l1.getKMin()[l2.getDirection()];
        int k1Max = l1.getKMax()[l2.getDirection()];

        int k2Min = l2.getKMin()[l1.getDirection()];
        int k2Max = l2.getKMax()[l1.getDirection()];

        return l2.getK() >= k1Min && l2.getK() < k1Max && l1.getK() >= k2Min && l1.getK() < k2Max;
    }

}
