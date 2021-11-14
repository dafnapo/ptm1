package com;

import test.Line;
import test.Point;

public class StatLib {
    // simple average
    public static float avg(float[] x) {
        int sum = 0;
        for (float v : x) sum += v;
        return (float) sum / x.length;
    }

    // returns the variance of X and Y
    public static float var(float[] x) {
        float sum = 0;
        for (float v : x) sum = sum + (float) Math.pow(v, 2.0);
        return (float) (sum * (1.0 / x.length) - Math.pow(avg(x), 2.0));
    }

    // returns the covariance of X and Y
    public static float cov(float[] x, float[] y) {
        float[] arr = new float[x.length];
        for (int i = 0; i < x.length; i++) {
            arr[i] = x[i] * y[i];
        }
        return (avg(arr) - (avg(x) * avg(y)));
    }


    // returns the Pearson correlation coefficient of X and Y
    public static float pearson(float[] x, float[] y) {
        float cov = cov(x, y);
        float absX = Math.abs(var(x));
        float absY = Math.abs(var(y));
        return (cov / (absX * absY));
    }

    // performs a linear regression and returns the line equation
    public static Line linear_reg(Point[] points) {


        return null;
    }

    // returns the deviation between point p and the line equation of the points
    public static float dev(Point p, Point[] points) {
        return 0;
    }

    // returns the deviation between point p and the line
    public static float dev(Point p, Line l) {
        return 0;
    }

}
