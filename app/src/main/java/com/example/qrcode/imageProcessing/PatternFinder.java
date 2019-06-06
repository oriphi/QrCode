package com.example.qrcode.imageProcessing;


import android.util.Log;

import com.example.qrcode.DebugMode;

import java.util.ArrayList;

public class PatternFinder {

    public static int THRESHOLD_1 = -20, THRESHOLD_2 = 10;
    public static double SCORE_MIN_1 = 0.32;

    private int status = 0;

    private byte[] array;
    private byte[] arrayDebug;

    private ArrayList<FinderGroup> finderGroups;
    private int bg, hg, hd;


    public PatternFinder(byte[] array, byte[] arrayDebug) {

        this.array = array;
        this.arrayDebug = arrayDebug;

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

            if(direction > 0) {
                boolean possible = false;
                for(int g = 0; g < finderGroups.size() && !possible; g++) {
                    if(k >= finderGroups.get(g).getKMin()[direction] && k < finderGroups.get(g).getKMax()[direction]) {
                        possible = true;
                    }
                }
                if(!possible) {
                    continue;
                }

            }


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
                    debugArray[3*j] = 127;
                    debugArray[3*j+1] = -128;
                    debugArray[3*j+2] = -128;
                } else if(newState == 1) {
                    debugArray[3*j] = -128;
                    debugArray[3*j+1] = 127;
                    debugArray[3*j+2] = -128;
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
                    if(direction < 2) {
                        if(n%2 == 0) {
                            debugArray[3*j2 + direction] = -128;
                        } else {
                            debugArray[3*j2 + direction] = 127;
                        }
                    }
                }
            }
*/
            //double[] intensities = new double[]{-1, -1, -1, -1, -1};

            for(int n = 0; n < borders.size()-5; n+=2) {

                double score = FinderLine.score(borders, n);

                if(score < SCORE_MIN_1)
                    continue;


                FinderLine line = new FinderLine(borders.get(n), borders.get(n+2), borders.get(n+3), borders.get(n+5), direction, k, (borders.get(n+2) - lineArrayIndices[0]) / lineArrayIndices[1],
                        (borders.get(n+3) - lineArrayIndices[0]) / lineArrayIndices[1], score);


                if(direction > 0) {

                    ArrayList<Integer> intersections = new ArrayList<Integer>();
                    for(int g = 0; g < finderGroups.size(); g++) {
                        if(finderGroups.get(g).intersect(line)) {
                            intersections.add(g);
                        }
                    }

                    if(intersections.size() > 0) {
                        for(int g = intersections.size()-1; g > 0; g--) {
                            FinderGroup group = finderGroups.remove((int)intersections.get(g));
                            finderGroups.get(intersections.get(0)).merge(group);
                        }
                        finderGroups.get(intersections.get(0)).addFinderLine(line);
                    } else {
                        finderGroups.add(new FinderGroup(line));
                    }

                } else {
                    finderGroups.add(new FinderGroup(line));
                }

            }

        }

    }

    public void findRightAngle() {

        for(FinderGroup group : finderGroups) {
            group.findBorders();
            group.calculateScore();
        }

        double score;
        double scoreBest = 0;

        for(int hg = 0; hg < finderGroups.size(); hg++) {

            score = finderGroups.get(hg).getScore();
            if(score <= scoreBest) {
                continue;
            }

            for(int bg = 1; bg < finderGroups.size(); bg++) {

                if(bg == hg)
                    continue;

                score = finderGroups.get(hg).getScore() * finderGroups.get(bg).getScore();
                if(score <= scoreBest) {
                    continue;
                }

                for(int hd = 0; hd < bg; hd++) {

                    if(hd == hg)
                        continue;

                    score = finderGroups.get(hg).getScore() * finderGroups.get(bg).getScore() * finderGroups.get(hd).getScore();
                    if(score <= scoreBest) {
                        continue;
                    }

                    double angle = MathUtil.angle(
                            finderGroups.get(hg).getCenter(),
                            finderGroups.get(hd).getCenter(),
                            finderGroups.get(bg).getCenter()
                    );

                    score *= Math.sin(angle);

                    //Log.d("SCORE", bg + "/" + hg + "/" + hd + " : " + score + " / " + scoreBest );
                    //Log.d("SCORE", finderGroups.get(bg).getCenter() + " / " + finderGroups.get(hg).getCenter() + " / " + finderGroups.get(hd).getCenter() + " : " + angle);


                    if(score > scoreBest) {
                        this.bg = bg;
                        this.hg = hg;
                        this.hd = hd;
                        scoreBest = score;
                    } else if(-score > scoreBest) {
                        this.bg = hd;
                        this.hg = hg;
                        this.hd = bg;
                        scoreBest = -score;
                    }

                }

            }

        }
    }

    public void analyze() {

        checkLines(0);


        //Log.d("NOMBRE DE GROUPES", String.valueOf(finderGroups.size()));

        checkLines(1);

        for(int g = finderGroups.size()-1; g >= 0; g--) {
            int miss = 0;
            for(int direction = 0; direction <= 1; direction++) {
                if(finderGroups.get(g).getLines(direction).size() == 0) {
                    miss++;
                }
            }
            if(miss >= 1) {
                finderGroups.remove(g);
            }
        }

        //Log.d("NOMBRE DE GROUPES", String.valueOf(finderGroups.size()));

        checkLines(2);

        for(int g = finderGroups.size()-1; g >= 0; g--) {
            int miss = 0;
            for(int direction = 0; direction <= 2; direction++) {
                if(finderGroups.get(g).getLines(direction).size() == 0) {
                    miss++;
                }
            }
            if(miss >= 1) {
                finderGroups.remove(g);
            }
        }
        //Log.d("NOMBRE DE GROUPES", String.valueOf(finderGroups.size()));

        checkLines(3);

        //Log.d("NOMBRE DE GROUPES", String.valueOf(finderGroups.size()));


        for(int g = finderGroups.size()-1; g >= 0; g--) {
            int miss = 0;
            for(int direction = 0; direction <= 3; direction++) {
                if(finderGroups.get(g).getLines(direction).size() == 0) {
                    miss++;
                }
            }
            if(miss >= 1) {
                finderGroups.remove(g);
            }

        }

        
        if(finderGroups.size() > 2) {
            status = 1;
        } else {
            status = -1;
            return;
        }

        //Log.d("NOMBRE DE GROUPES", String.valueOf(finderGroups.size()));
/*
        for(int i = 0; i < array.length; i++) {
            array[i] = -128;
        }
*/

        findRightAngle();
/*
        for(int j = 0; j < array.length; j++) {
            array[j] = -128;
        }
*/
        if(DebugMode.DEBUG_MODE) {

            for(int g = 0; g < finderGroups.size(); g++) {
                byte v1, v2, v3;
                if(g == bg) {
                    v1 = (byte)255;
                    v2 = 0;
                    v3 = 0;
                } else if(g == hg) {
                    v1 = 0;
                    v2 = (byte)255;
                    v3 = 0;
                } else if(g == hd) {
                    v1 = 0;
                    v2 = 0;
                    v3 = (byte)255;
                } else {
                    v1 = (byte)255;
                    v2 = (byte)255;
                    v3 = 0;
                }
                for(int direction = 0; direction < 4; direction++) {
                    int increment = lineArrayIndices(direction, 0)[1];
                    for(FinderLine line : finderGroups.get(g).getLines(direction)) {
                        for(int j = line.getJ2(); j < line.getJ3(); j += increment) {
                            arrayDebug[3*j] = v1;
                            arrayDebug[3*j+1] = v2;
                            arrayDebug[3*j+2] = v3;
                        }
                    }

                }

            }

        }
        /*
        Log.d("BG", bg + " : " + finderGroups.get(bg).getCenter().toString());
        Log.d("HG", hg + " : " + finderGroups.get(hg).getCenter().toString());
        Log.d("HD", hd + " : " + finderGroups.get(hd).getCenter().toString());
        */

    }

    public FinderGroup getBg() {
        return finderGroups.get(bg);
    }

    public FinderGroup getHg() {
        return finderGroups.get(hg);
    }

    public FinderGroup getHd() {
        return finderGroups.get(hd);
    }

    public int getStatus() {
        return status;
    }

}
