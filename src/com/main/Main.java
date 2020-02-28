package com.main;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String inputDirPath = "data/in/family_self/";
        String outputFilePath = "data/out/families_mean_stdev.txt";
        File folder = new File(inputDirPath);
        File[] FilesList = folder.listFiles();

        List<BigDecimal> scores = new ArrayList<>();

        try {
            FileWriter fw = new FileWriter(outputFilePath);
            for (File file : FilesList) {
                if (file.isFile()) {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    String line;

                    // read in each line, add similarity score to List
                    while ((line = br.readLine()) != null) {
                        String[] columns = line.split("\t");
                        scores.add(new BigDecimal(columns[columns.length - 1]));
                    }
                    fr.close();

                    //write to output
                    String familyName = file.getName().substring(0, file.getName().indexOf("."));
                    String str = familyName + "\t" + mean(scores) + "\t" + stdev(scores) + "\n";
                    fw.write(str);
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static BigDecimal mean(List<BigDecimal> x) {
        MathContext mc = new MathContext(10);
        BigDecimal sum = new BigDecimal("0");
        BigDecimal size = new BigDecimal(Integer.toString(x.size()));
        for (BigDecimal y : x) {
            sum = sum.add(y, mc);
        }
        return sum.divide(size, new MathContext(4));
    }

    public static BigDecimal stdev(List<BigDecimal> x) {
        MathContext mc = new MathContext(10);
        BigDecimal mean = mean(x);
        BigDecimal temp = new BigDecimal("0");
        BigDecimal size = new BigDecimal(Integer.toString(x.size()));

        for (BigDecimal val : x) {
            BigDecimal squrDiffToMean = val.subtract(mean, mc).pow(2, mc);
            temp = temp.add(squrDiffToMean, mc);
        }

        BigDecimal meanOfDiffs = temp.divide(size, mc);
        return meanOfDiffs.sqrt(new MathContext(4));
    }
}


