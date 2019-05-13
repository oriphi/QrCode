package com.example.qrcode.imageProcessing;


import org.opencv.core.Mat;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PatternFinder {

    public static int THRESHOLD_1 = -20, THRESHOLD_2 = 12;
    public static double SCORE_MIN_1 = 0.32;


    private byte[] array;

    private ArrayList<FinderGroup> finderGroups;


    public PatternFinder(byte[] array) {

        this.array = array;

        this.finderGroups = new ArrayList<FinderGroup> ();

        analyze();

    }

    private int[] lineArrayIndices(int direction, int k) {

        int jStart;
        int jIncrement;
        int iMax;

        if(direction == 0) {
            jStart = k * QrDetector.IMAGE_WIDTH;
            jIncrement = 1;
            iMax = QrDetector.IMAGE_WIDTH;
        } else if (direction == 1) {
            jStart = k;
            jIncrement = QrDetector.IMAGE_WIDTH;
            iMax = QrDetector.IMAGE_HEIGHT;
        } else if (direction == 2) {
            k -= QrDetector.IMAGE_HEIGHT - 1;
            if (k >= 0) {
                jStart = k;
                iMax = Math.min(QrDetector.IMAGE_WIDTH - k, QrDetector.IMAGE_HEIGHT);
            } else {
                jStart = -k * QrDetector.IMAGE_WIDTH;
                iMax = Math.min(QrDetector.IMAGE_HEIGHT + k, QrDetector.IMAGE_WIDTH);
            }
            jIncrement = QrDetector.IMAGE_WIDTH + 1;
        } else {
            k -= QrDetector.IMAGE_WIDTH - 1;
            if(k >= 0) {
                jStart = k * QrDetector.IMAGE_WIDTH + QrDetector.IMAGE_WIDTH - 1;
                iMax = Math.min(QrDetector.IMAGE_HEIGHT - k, QrDetector.IMAGE_WIDTH);
            } else {
                jStart = QrDetector.IMAGE_WIDTH - 1 + k;
                iMax = Math.min(QrDetector.IMAGE_WIDTH + k, QrDetector.IMAGE_HEIGHT);
            }
            jIncrement = QrDetector.IMAGE_WIDTH - 1;
        }

        return new int[]{jStart, jIncrement, iMax};

    }


    private void checkLines(int direction) {

        int kMax;
        if(direction == 0) {
            kMax = QrDetector.IMAGE_HEIGHT;
        } else if (direction == 1) {
            kMax = QrDetector.IMAGE_WIDTH;
        } else {
            kMax = QrDetector.IMAGE_WIDTH + QrDetector.IMAGE_HEIGHT - 1;
        }

        for(int k = 0; k < kMax; k++) {

            int[] lineArrayIndices = lineArrayIndices(direction, k);
            int j = lineArrayIndices[0];

            int newState = -1;
            int state = -1;

            int jDMax = 0;
            int d = 0;
            int dMax = 0;

            int jLast = -1;

            int value = 0;
            int lastValue = 0;

            ArrayList<Integer> borders = new ArrayList<>();

            for(int i = 0; i < lineArrayIndices[2]; i++) {

                value = array[j];

                if(value < THRESHOLD_1) {
                    newState = 0;
                } else if(value > THRESHOLD_2) {
                    newState = 1;
                } else {
                    newState = -1;
                }
/*
                if(newState == 0) {
                    array[j] = -128;
                } else if(newState == 1) {
                    array[j] = 127;
                }
*/
                if(state == 0) {
                    d = value - lastValue;
                } else if(state == 1) {
                    d = lastValue - value;
                }
                if(d > dMax) {
                    dMax = d;
                    jDMax = j;
                }

                if(newState >= 0) {

                    if(state == 1 - newState) {

                        borders.add(jDMax);

                    } else if(state == -1 && newState == 0) {

                        borders.add(j);

                    }

                    jLast = j;
                    dMax = 0;

                    state = newState;
                }

                lastValue = value;
                j += lineArrayIndices[1];
            }

            if(jLast >= 0) {
                borders.add(jLast + lineArrayIndices[1]);
            }
/*
            for(int n = 0; n < borders.size()-1; n++) {
                for(int j2 = borders.get(n); j2 < borders.get(n+1); j2 += lineArrayIndices[1]) {
                    if(n%2 == 0) {
                        array[j2] = -128;
                    } else {
                        array[j2] = 127;
                    }
                }
            }
*/
            //double[] intensities = new double[]{-1, -1, -1, -1, -1};

            for(int n = 0; n < borders.size()-5; n+=2) {
                /*
                for(int i = 0; i < 3; i++) {
                    intensities[i] = intensities[i+2];
                }
                intensities[3] = -1;
                intensities[4] = -1;
                */
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

                if(score < SCORE_MIN_1)
                    continue;


                //TODO: rajouter le calcul d'intensité

                for(int j2 = borders.get(n+2); j2 < borders.get(n+3); j2 += lineArrayIndices[1]) {

                    array[j2] = -128;

                }

            }

        }



    }

    private void checkAllLines() {

    }

    public void analyze() {

        checkLines(3);

    }

}
