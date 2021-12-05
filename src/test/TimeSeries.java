package test;

import java.io.BufferedReader;
import java.io.FileReader;

public class TimeSeries {
    private final String fileName;
    private float[][] dataFile;
    private String[] criteriaTitles;
    int numOfRows = 0;
    public double threshold = 0.9;

    public TimeSeries(String csvFileName) {
        this.fileName = csvFileName;
    }

    public String getCriteriaTitle(int index) {
        return criteriaTitles[index];
    }

    void readCsvFile() {
        int numOfCriteria = 0, counter = 0;
        String line, delimiter = ",";
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.fileName));
            if ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                numOfCriteria = values.length;
                numOfRows = getNumOfLines(fileName);
                dataFile = new float[numOfRows][numOfCriteria];
                criteriaTitles = new String[values.length];
                System.arraycopy(values, 0, criteriaTitles, 0, numOfCriteria);
            }
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                for (int i = 0; i < numOfCriteria; i++) {
                    dataFile[counter][i] = Float.parseFloat(values[i]);
                }
                counter++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getNumOfLines(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        int counter = 0;
        while (br.readLine() != null)
            counter++;
        return counter - 1;
    }

    public float[][] getDataFile() {
        return this.dataFile;
    }

    public float[] getMatrixColumn(int index) {
        float[] column = new float[numOfRows];
        for (int row = 0; row < numOfRows; row++) {
            column[row] = dataFile[row][index];
        }
        return column;
    }

    public int getColumnIndexOfCriteria(String criteria) {
        for (int i = 0; i < criteriaTitles.length; i++) {
            if (criteria.equals(criteriaTitles[i])) {
                return i;
            }
        }
        return -1;
    }
}
