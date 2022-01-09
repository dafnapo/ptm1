package test;

import java.io.*;
import java.util.*;

public class TimeSeries {
    Map<String, float[]> map = new HashMap<>();
    String[] names;

    public TimeSeries(String csvFileName) {
        try {
            Scanner s = new Scanner
                    (new BufferedReader(new FileReader(csvFileName)));
            Map<Integer, Vector<Float>> map1 = new HashMap<>();
            names = s.next().split(",");
            for (int i = 0; i < names.length; i++) {
                map1.put(i, new Vector<>());
            }
            while (s.hasNextLine()) {
                String[] row = s.nextLine().split(",");
                for (int i = 0; i < row.length; i++) {
                    if (!row[i].isEmpty())
                        map1.get(i).add(Float.parseFloat(row[i]));
                }
            }
            for (int i = 0; i < names.length; i++) {
                map.put(names[i], vectorToFloatArray(i, map1));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public float[] vectorToFloatArray(int i, Map<Integer, Vector<Float>> map) {
        int index = 0;
        Vector<Float> needed = map.get(i);
        float[] column = new float[needed.size()];
        for (Float f : needed) {
            column[index++] = f;
        }
        return column;
    }

    public float[] getColumn(String s) {
        return map.get(s);
    }

    public float getValueAt(String j, int i) {
        return getColumn(j)[i];
    }
}