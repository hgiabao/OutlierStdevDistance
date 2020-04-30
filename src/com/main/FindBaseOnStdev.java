package com.main;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//Get avg and stdev of similarity scores for each family.
//Output file syntax: <excluded species> <avg_rest_of_cluster> <stdev_rest_of_cluster> <avg_excluded_species> <#_of_stdev_away>
public class FindBaseOnStdev {

    private String str;

    public static void main(String[] args) {
        String inputDirPath = "data/out/genus/";
        String outputFilePath = "data/out/genus_filter.csv";
        File folder = new File(inputDirPath);

        File[] FilesList = folder.listFiles();

        try {
            FileWriter fw = new FileWriter(outputFilePath);
            for (File file : FilesList) {
                List<BigDecimal> scores = new ArrayList<>();
                if (file.isFile()) {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    String line;
                    int lines = 0;

                    while ((line = br.readLine()) != null) {
                        lines++;
                        String[] columns = line.split("\t");
                        if (!columns[columns.length - 1].equals("#DIV/0")) {
                            scores.add(new BigDecimal(columns[columns.length - 1]));
                        }
                    }
                    fr.close();

                    String familyName = file.getName().substring(0, file.getName().indexOf("."));
                    String str;
                    if (filter(scores) && lines >= 5) {
                        str = familyName + "\t" + "TRUE" + "\n";
                    } else {
                        str = familyName + "\t" + "FALSE" + "\n";
                    }
                    fw.write(str);
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean filter(List<BigDecimal> list) {
        for (BigDecimal a : list) {
            if (a.compareTo(new BigDecimal(1.8)) >= 0) {
                return false;
            }
        }
        return true;
    }
}
