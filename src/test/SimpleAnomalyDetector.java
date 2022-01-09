package test;

import java.util.ArrayList;
import java.util.List;


public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
    public float CORRELATION_THRESHOLD = (float) 0.9;
    List<CorrelatedFeatures> correlatedFeatures = new ArrayList<>();

    float p(int i, int j, TimeSeries ts) {
        return StatLib.pearson(ts.getColumn(ts.names[i]), ts.getColumn(ts.names[j]));
    }

    @Override
    public void learnNormal(TimeSeries ts) {
        for (int i = 0; i < ts.names.length - 1; i++) {
            float m = CORRELATION_THRESHOLD;
            int c = -1;
            float p;
            for (int j = i + 1; j < ts.names.length; j++) {
                if ((p = Math.abs(p(i, j, ts))) > m) {
                    m = p;
                    c = j;
                }
            }
            if (c != (-1)) {

                Point[] points = new Point[ts.getColumn(ts.names[i]).length];
                for (int j = 0; j < ts.getColumn(ts.names[i]).length; j++) {
                    points[j] = new Point
                            (ts.getColumn(ts.names[i])[j], ts.getColumn(ts.names[c])[j]);
                }
                Line l = StatLib.linear_reg(points);
                float max = 0;
                for (int j = 0; j < ts.getColumn(ts.names[i]).length; j++) {
                    if (max < Math.abs(StatLib.dev(points[j], l))) {
                        max = Math.abs(StatLib.dev(points[j], l));
                    }
                }
                correlatedFeatures.add(new CorrelatedFeatures
                        (ts.names[i], ts.names[c], m, l, (float) (max * 1.1)));
            }
        }

    }


    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        learnNormal(ts);
        List<AnomalyReport> anomalyList = new ArrayList<>();


        Point[] points = new Point[ts.getColumn(ts.names[0]).length];


        for (int i = 0; i < correlatedFeatures.size(); i++) {
            for (int j = 0; j < ts.getColumn(ts.names[0]).length; j++) {
                points[j] = new Point
                        (ts.getColumn(correlatedFeatures.get(i).feature1)[j],
                                ts.getColumn(correlatedFeatures.get(i).feature2)[j]);
            }
            for (int j = 0; j < points.length; j++) {
                if (StatLib.dev(points[j], correlatedFeatures.get(i).lin_reg) > correlatedFeatures.get(i).threshold) {
                    anomalyList.add(new AnomalyReport
                            (correlatedFeatures.get(i).feature1 + "-" + correlatedFeatures.get(i).feature2, 1 + j));
                }
            }
        }
        return anomalyList;
    }

    public List<CorrelatedFeatures> getNormalModel() {
        return correlatedFeatures;
    }
}