package test;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Commands {

    // Default IO interface
    public interface DefaultIO {
        public String readText();

        public void write(String text);

        public float readVal();

        public void write(float val);
    }

    // the default IO to be used in all commands
    DefaultIO dio;

    public Commands(DefaultIO dio) {
        this.dio = dio;
    }

    // you may add other helper classes here
    class Anomalies {
        int start, end, length;

        public Anomalies(int start, int end, int length) {
            this.start = start;
            this.end = end;
            this.length = length;
        }
    }

    // the shared state of all commands
    private class SharedState {
        // implement here whatever you need
        SimpleAnomalyDetector anomalyDetector = new SimpleAnomalyDetector();
        List<AnomalyReport> anomalyList = new ArrayList<>();
        int anomalyStart, anomalyLength;
        List<Anomalies> anomalies = new ArrayList<>();
        int n = 0;

    }

    private SharedState sharedState = new SharedState();

    // Command class for example:
    public class ExampleCommand extends Command {

        public ExampleCommand() {
            super("this is an example of command\n");
        }

        @Override
        public void execute() {
            dio.write(description);
        }
    }

    // Command abstract class
    public abstract class Command {
        protected String description;

        public Command(String description) {
            this.description = description;
        }

        public abstract void execute();
    }

    // implement here all the other commands

    public class uploadTimeSeries extends Command {
        public uploadTimeSeries() {
            super("1. upload a time series csv file\n");
        }

        PrintWriter trainCSV;
        PrintWriter testCSV;

        {
            try {
                trainCSV = new PrintWriter(new FileWriter("anomalyTrain.csv"));
                testCSV = new PrintWriter(new FileWriter("anomalyTest.csv"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void execute() {
            dio.write("Please upload your local train CSV file.\n");
            String line = dio.readText();
            while (!line.equals("done")) {
                trainCSV.println(line);
                trainCSV.flush();
                line = dio.readText();
            }
            dio.write("Upload complete.\n");

            dio.write("Please upload your local test CSV file.\n");
            line = dio.readText();
            while (!line.equals("done")) {
                testCSV.println(line);
                testCSV.flush();
                line = dio.readText();
                sharedState.n++;
            }
            dio.write("Upload complete.\n");

        }
    }

    public class algoSettings extends Command {
        public algoSettings() {
            super("2. algorithm settings\n");
        }

        @Override
        public void execute() {
            dio.write("The current correlation threshold is " + sharedState.anomalyDetector.CORRELATION_THRESHOLD + "\n");
            dio.write("Type a new threshold\n");
            float newThresh = dio.readVal();
            while (newThresh <= 0 || newThresh >= 1) {
                dio.write("please choose a value between 0 and 1.\n");
                newThresh = dio.readVal();
            }
            sharedState.anomalyDetector.CORRELATION_THRESHOLD = newThresh;
        }
    }

    public class detectAnomalies extends Command {
        public detectAnomalies() {
            super("3. detect anomalies\n");
        }

        @Override
        public void execute() {
            TimeSeries train = new TimeSeries("anomalyTrain.csv");
            TimeSeries test = new TimeSeries("anomalyTest.csv");
            sharedState.anomalyDetector.learnNormal(train);
            sharedState.anomalyList = sharedState.anomalyDetector.detect(test);
            dio.write("anomaly detection complete.\n");
        }
    }

    public class displayResults extends Command {
        public displayResults() {
            super("4. display results\n");
        }

        @Override
        public void execute() {
            for (AnomalyReport ar : sharedState.anomalyList) {
                dio.write(ar.timeStep + "\t" + ar.description + "\n");
            }
            dio.write("Done.\n");
        }
    }

    public class uploadAnomaliesAnalyzeResults extends Command {
        public uploadAnomaliesAnalyzeResults() {
            super("5. upload anomalies and analyze results\n");
        }

        @Override
        public void execute() {

            sharedState.anomalies.clear();
            for (int i = 0; i < sharedState.anomalyList.size() - 1; i++) {
                sharedState.anomalyStart = (int) sharedState.anomalyList.get(i).timeStep;
                sharedState.anomalyLength = 1;
                while ((i <= (sharedState.anomalyList.size() - 2)) &&
                        (sharedState.anomalyList.get(i).description.equals
                                (sharedState.anomalyList.get(i + 1).description))) {
                    sharedState.anomalyLength++;
                    i++;
                }
                sharedState.anomalies.add(new Anomalies(sharedState.anomalyStart,
                        (int) sharedState.anomalyList.get(i).timeStep, sharedState.anomalyLength));
            }

            List<Anomalies> userAnomalies = new ArrayList<>();
            dio.write("Please upload your local anomalies file.\n");

            PrintWriter userAnomaliesCSVWriter = null;

            {
                try {
                    userAnomaliesCSVWriter = new PrintWriter(new FileWriter("userAnomalies.csv"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String line = dio.readText();
            while (!line.equals("done")) {
                assert userAnomaliesCSVWriter != null;
                userAnomaliesCSVWriter.println(line);
                userAnomaliesCSVWriter.flush();
                line = dio.readText();
            }

            dio.write("Upload complete.\n");

            Scanner userAnomalyCSV = null;
            try {
                userAnomalyCSV = new Scanner
                        (new BufferedReader(new FileReader("userAnomalies.csv")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (true) {
                assert userAnomalyCSV != null;
                if (!userAnomalyCSV.hasNextLine()) break;
                line = userAnomalyCSV.nextLine();
                String[] times = line.split(",");
                if (times.length != 2) continue;
                int a = Integer.parseInt(times[0]);
                int b = Integer.parseInt(times[1]);
                userAnomalies.add(new Anomalies(a, b, b - a + 1));
            }

            int TP = 0;


            for (Anomalies anomaly : sharedState.anomalies) {
                for (int i = 0; i < userAnomalies.size(); i++) {
                    if ((anomaly.start > userAnomalies.get(i).end) ||
                            (anomaly.end < userAnomalies.get(i).start))
                        continue;
                    TP++;
                    break;
                }
            }

            sharedState.n -= 1;
            float P = userAnomalies.size();
            float N = sharedState.n;

            for (Anomalies ua : userAnomalies) {
                N -= ua.length;
            }
            int FP = sharedState.anomalies.size() - TP;

            DecimalFormat df = new DecimalFormat("#0.0##");
            df.setRoundingMode(RoundingMode.DOWN);

            float tpr = TP / P;
            String TPR = df.format(tpr);
            float far = FP / N;
            String FAR = df.format(far);
            dio.write("True Positive Rate: " + TPR + "\n");
            dio.write("False Positive Rate: " + FAR + "\n");
        }
    }
}