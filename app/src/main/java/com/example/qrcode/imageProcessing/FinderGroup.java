package com.example.qrcode.imageProcessing;

import android.util.Log;

import org.opencv.core.Point;

import java.util.ArrayList;

public class FinderGroup {

    private ArrayList<FinderLine>[] lines;

    private int[] kMin, kMax;

    private Point[] borders;
    private double xCenter, yCenter;

    private double score;

    public FinderGroup(FinderLine line) {

        lines = new ArrayList[4];

        for(int d = 0; d < 4 ; d++) {
            lines[d] = new ArrayList<FinderLine>();
        }

        kMin = new int[]{99999, 99999, 99999, 99999};
        kMax = new int[4];

        addFinderLine(line);
    }

    public void merge(FinderGroup group) {
        for(int direction = 0; direction < 4; direction++) {
            for(FinderLine line : group.getLines(direction)) {
                addFinderLine(line);
            }
        }
    }

    public void addFinderLine(FinderLine line) {

        int direction = line.getDirection();

        lines[direction].add(line);

        for(int d = 0; d < 4; d++) {
            kMin[d] = Math.min(kMin[d], line.getKMin()[d]);
            kMax[d] = Math.max(kMax[d], line.getKMax()[d]);
        }

    }

    public void findBorders() {

        int size = lines[0].size()+lines[1].size()+lines[2].size()+lines[3].size();
        borders = new Point[2*size];

        int count = 0;

        for(int d = 0; d < 4; d++) {
            for(FinderLine line : lines[d]) {

                borders[count] = new Point(line.getJ1() % QrDetector.IMAGE_WIDTH, line.getJ1() / QrDetector.IMAGE_WIDTH);
                borders[count+1] = new Point(line.getJ4() % QrDetector.IMAGE_WIDTH, line.getJ4() / QrDetector.IMAGE_WIDTH);

                count += 2;
            }
        }

        xCenter = 0;
        yCenter = 0;
        for(int i = 0; i < 2*size; i++) {
            xCenter += borders[i].x;
            yCenter += borders[i].y;
        }
        xCenter /= 2*size;
        yCenter /= 2*size;
    }

    public void calculateScore() {

        // moyenne géométrique des scores de chaque ligne
        double score2 = 1;

        int count = 0;
        for (int direction = 0; direction < 4; direction++) {
            for (FinderLine line : lines[direction]) {
                score2 *= line.getScore();
                count += 1;
            }
        }
        score2 = Math.pow(score2, 1.0/count);

        // compacité du centre noir
        double score1 = 1;
        int dmin = 0;
        double smin = 2;
        double[] s = new double[4];
        for (int direction = 0; direction < 4; direction++) {
            s[direction] = 1.0 * lines[direction].size() / (kMax[direction] - kMin[direction]);
            if(s[direction] < smin) {
                smin = s[direction];
                dmin = direction;
            }
            //Log.d("DIRECTION " + direction, kMax[direction] + " / " + kMin[direction] + " / " + lines[direction].size() + " / " + s[direction]);
        }
        for (int direction = 0; direction < 4; direction++) {
            if(direction != dmin) {
                score1 *= s[direction];
            }
        }
        //Log.d("SCORE", String.valueOf(score1));

        // lignes de même direction doivent faire la même taille
        double score3 = 1;
        for (int direction = 0; direction < 4; direction++) {
            double sc = 1;

            double[] len = new double[lines[direction].size()];
            double moy = 1;
            for (int i = 0; i < lines[direction].size(); i++) {
                len[i] = lines[direction].get(i).getIMax() - lines[direction].get(i).getIMin();
                moy *= Math.pow(len[i], 1.0 / lines[direction].size());
            }
            for (int i = 0; i < lines[direction].size(); i++) {
                if(len[i] > moy) {
                    sc *= moy / len[i];
                } else {
                    sc *= len[i] / moy;
                }
            }
            sc = Math.pow(sc, 1.0 / lines[direction].size());

            score3 *= sc;
        }

        //Log.d("SCORE", String.valueOf(group.getScore()));
        //Log.d("SCORE", score1 + " / " + score2 + " / " + score3);

        score = score1 * score2 * score3;
    }

    public double getScore() {
        return score;
    }


    public ArrayList<FinderLine> getLines(int direction) {
        return lines[direction];
    }

    public int[] getKMin() {
        return kMin;
    }
    public int[] getKMax() {
        return kMax;
    }

    public boolean intersect(FinderLine line) {

        int lineDirection = line.getDirection();

        if(line.getK() >= kMin[lineDirection] && line.getK() < kMax[lineDirection]) {

            for(int direction = 0; direction < 4; direction++) {

                if(direction == line.getDirection())
                    continue;

                for(FinderLine line2 : lines[direction]) {

                    if(FinderLine.intersect(line, line2)) {
                        return true;
                    }

                }
            }

        }

        return false;

    }

    public Point getCenter() {
        return new Point(xCenter, yCenter);
    }

    public Point[] getBorders() {
        return borders;
    }


}
