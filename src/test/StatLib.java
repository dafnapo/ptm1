package test;

public class StatLib {
    // simple average
    public static float avg(float[] x) {
        float sum = 0;
        for (float v : x) sum += v;
        return sum / x.length;
    }

    // returns the variance of X and Y
    public static float var(float[] x) {
        float sum = 0;
        float avgX = avg(x);
        for (float v : x) {
            sum += Math.pow((v - avgX), 2.0);
        }
        return (sum / x.length);
    }

    // returns the covariance of X and Y
    public static float cov(float[] x, float[] y) {
        float sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += (x[i] - avg(x)) * (y[i] - avg(y));
        }
        return sum / x.length;
    }


    // returns the Pearson correlation coefficient of X and Y
    public static float pearson(float[] x, float[] y) {
        float cov = cov(x, y);
        float sqrtX = (float) Math.sqrt(var(x));
        float sqrtY = (float) Math.sqrt(var(y));
        return (cov / (sqrtX * sqrtY));
    }

    // performs a linear regression and returns the line equation
    public static Line linear_reg(Point[] points) {
        float[] arrX = new float[points.length];
        float[] arrY = new float[points.length];
        for (int i = 0; i < arrX.length; i++) {
            arrX[i] = points[i].x;
        }
        for (int i = 0; i < arrY.length; i++) {
            arrY[i] = points[i].y;
        }
        float a = (cov(arrX, arrY) / var(arrX));
        float b = (avg(arrY) - (a * avg(arrX)));
        return new Line(a, b);
    }

    // returns the deviation between point p and the line equation of the points
    public static float dev(Point p, Point[] points) {
        Line newLine = linear_reg(points);
        return dev(p, newLine);
    }

    // returns the deviation between point p and the line
    public static float dev(Point p, Line l) {
        return Math.abs(l.f(p.x) - p.y);
    }

}
