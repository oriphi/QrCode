package com.example.qrcode.imageProcessing;

import org.opencv.core.Point;

public class MathUtil {

/*
    public static double angleBetweenVectors(Point v1, Point v2) {
        return angleBetweenVectors(v1.x, v1.y, v2.x, v2.y);
    }

    public static double angleBetweenVectors(double dx1, double dy1, double dx2, double dy2) {
        return Math.atan2(- dx1 * dy2 + dy1 * dx2, dx1 * dx2 + dy1 * dy2);
    }
*/
    public static double angle(Point center, Point current, Point previous) {

        return Math.atan2(current.x - center.x, current.y - center.y) - Math.atan2(previous.x- center.x,previous.y- center.y);

    }


    public static Point intersectLines(Point p1, Point p2, Point p3, Point p4) {

        Point d1 = new Point(p1.x - p2.x, p1.y - p2.y);
        Point d2 = new Point(p3.x - p4.x, p3.y - p4.y);

        double div = d1.x * d2.y - d1.y * d2.x;

        /*
        if (div == 0) {
            raise Exception('lines do not intersect')
        }
        */

        double c1 = p1.x * p2.y - p1.y * p2.x;
        double c2 = p3.x * p4.y - p3.y * p4.x;

        double x = (c1 * d2.x - c2 * d1.x) / div;
        double y = (c1 * d2.y - c2 * d1.y) / div;

        return new Point(x, y);

    }



}
