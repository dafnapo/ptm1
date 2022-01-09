package test;

public class StatLib {
    // simple average
    public static float avg(float[] x) {
        float sum = 0;
        int numbers = 0;
        for (float num : x) {
            sum += num;
            numbers++;
        }
        return (sum / numbers);
    }

    // returns the variance of X and Y
    public static float var(float[] x) {
        float average = avg(x);
        float sum = 0;
        int numbers = 0;
        for (float num : x) {
            sum += Math.pow(num - average, 2);
            numbers++;
        }
        return (sum / numbers);
    }

    // returns the covariance of X and Y
    public static float cov(float[] x, float[] y) {
        float avgX = avg(x), avgY = avg(y), sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += ((x[i] - avgX) * (y[i] - avgY));
        }
        return (sum / x.length);
    }


    // returns the Pearson correlation coefficient of X and Y
    public static float pearson(float[] x, float[] y) {
        return (float) (cov(x, y) / (Math.sqrt(var(x)) * Math.sqrt(var(y))));
    }

    // performs a linear regression and returns the line equation
    public static Line linear_reg(Point[] points) {
        float a, b;
        float[] x = new float[points.length];
        float[] y = new float[points.length];

        for (int i = 0; i < points.length; i++) {
            x[i] = points[i].x;
            y[i] = points[i].y;
        }

        a = (cov(x, y) / var(x));
        b = (avg(y) - (a * avg(x)));
        return new Line(a, b);
    }

    // returns the deviation between point p and the line equation of the points
    public static float dev(Point p, Point[] points) {
        Line line = linear_reg(points);
        float y = line.f(p.x);

        return Math.abs(p.y - y);
    }

    // returns the deviation between point p and the line
    public static float dev(Point p, Line l) {
        float y = l.f(p.x);

        return Math.abs(p.y - y);
    }
}