package com.example.qrcode.imageProcessing;

import java.util.ArrayList;

public class FinderGroup {

    private ArrayList<FinderLine>[] lines;

    private int[] kMin, kMax;


    public FinderGroup(FinderLine line) {

        lines = new ArrayList[4];

        for(int d = 0; d < 4 ; d++) {
            lines[d] = new ArrayList<FinderLine>();
        }

        kMin = new int[4];
        kMax = new int[4];

        addFinderLine(line);
    }

    public void addFinderLine(FinderLine line) {

        int direction = line.getDirection();

        lines[direction].add(line);
        for(int d = 0; d < 4; d++) {
            kMin[d] = Math.min(kMin[d], line.getKMin()[d]);
            kMax[d] = Math.max(kMax[d], line.getKMax()[d]);
        }

    }

    public int[] getKMin() {
        return kMin;
    }
    public int[] getKMax() {
        return kMax;
    }

    public boolean intersect(FinderLine line) {

        for(int direction = 0; direction < 4; direction++) {

            for(FinderLine line2 : lines[direction]) {

                if(FinderLine.intersect(line, line2)) {
                    return true;
                }

            }
        }

        return false;

    }

}
