package test;

import java.util.LinkedList;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
    private List<CorrelatedFeatures> correlatedFeatures = new LinkedList<CorrelatedFeatures>();

    @Override
    public void learnNormal(TimeSeries ts) {
        try {
            ts.readCsvFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        float[][] data = ts.getDataFile();
        for (int j = 0; j < data.length; j++) {
            float m = 0, c = -1;
            for (int i = j + 1; i < data[j].length; i++) {
                float p = Math.abs(StatLib.pearson(ts.getMatrixColumn(j), ts.getMatrixColumn(i)));
                if (p > m) {
                    m = p;
                    c = i;
                }
            }
            if (c != -1 && m * 1.1 > ts.threshold) {
                Point[] points = getPointFromCorrelatedColumns(ts.getMatrixColumn(j), ts.getMatrixColumn((int) c));
                Line line = StatLib.linear_reg(points);
                correlatedFeatures.add(new CorrelatedFeatures(ts.getCriteriaTitle(j),
                        ts.getCriteriaTitle((int) c), m, line,
                        (float) getMaxDeviation(line, points)));
            }
        }
    }

    private Object getMaxDeviation(Line line, Point[] points) {
        float result = 0;
        for (Point point : points) {
            float deviation = StatLib.dev(point, line);
            if (deviation > result)
                result = deviation;
        }
        return result;
    }

    private Point[] getPointFromCorrelatedColumns(float[] x, float[] y) {
        Point[] result = new Point[x.length];
        for (int i = 0; i < x.length; i++) {
            Point temp = new Point(x[i], y[i]);
            result[i] = temp;
        }
        return result;
    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        List<AnomalyReport> report = new LinkedList<AnomalyReport>();
        float[][] data = new float[0][];
        try {
            ts.readCsvFile();
            data = ts.getDataFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < ts.numOfRows; i++) {
            for (CorrelatedFeatures cf : correlatedFeatures) {
                Point tempPoint = new Point(data[i][ts.getColumnIndexOfCriteria(cf.feature1)],
                        data[i][ts.getColumnIndexOfCriteria(cf.feature2)]);
                if (StatLib.dev(tempPoint, cf.lin_reg) > cf.threshold*1.1) {
                    report.add(new AnomalyReport(cf.feature1 + "-" + cf.feature2, i + 1));
                }
            }
        }
        return report;
    }

    public List<CorrelatedFeatures> getNormalModel() {
        return correlatedFeatures;
    }
}
