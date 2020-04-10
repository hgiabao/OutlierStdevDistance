package com.main;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

//Get avg and stdev of similarity scores for each family.
//Output file syntax: <excluded species> <avg_rest_of_cluster> <stdev_rest_of_cluster> <avg_excluded_species> <#_of_stdev_away>
public class FamiliesAvgStdev {

    private String str;

    public static void main(String[] args) {
        String inputDirPath = "data/in/family_self/";
        String outputFilePath = "data/out/families_mean_stdev.txt";
        File folder = new File(inputDirPath);

        File[] FilesList = folder.listFiles();
        List<String> speciesName = new ArrayList<>();

        try {
            FileWriter fw = new FileWriter(outputFilePath);
            for (File file : FilesList) {
                List<BigDecimal> scores = new ArrayList<>();
                if (file.isFile()) {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    String line;

                    while ((line = br.readLine()) != null) {
                        String[] columns = line.split("\t");
                        scores.add(new BigDecimal(columns[columns.length - 1]));
                    }
                    fr.close();

                    String familyName = file.getName().substring(0, file.getName().indexOf("."));
                    BigDecimal Avg = mean(scores, 4);
                    BigDecimal Stdev = stdev(scores,4);
                    String str = familyName + "\t" + Avg + "\t" + Stdev + "\n";
                    fw.write(str);
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BigDecimal meanPair(List<speciesNameAndScorePair> x, int mathContext) {
        BigDecimal sum = new BigDecimal("0");
        BigDecimal size = new BigDecimal(Integer.toString(x.size()));
        for (int i = 0; i < x.size(); i++) {
            sum = sum.add(x.get(i).getScore());
        }
        return sum.divide(size, new MathContext(mathContext));
    }

    public static BigDecimal mean(List<BigDecimal> x, int mathContext) {
        BigDecimal sum = new BigDecimal("0");
        BigDecimal size = new BigDecimal(Integer.toString(x.size()));
        for (int i = 0; i < x.size(); i++) {
            sum = sum.add(x.get(i));
        }
        return sum.divide(size, new MathContext(mathContext));
    }

    public static BigDecimal stdevPair(List<speciesNameAndScorePair> x, int mathContext) {
        MathContext mc = new MathContext(7);
        BigDecimal mean = meanPair(x, 7);
        BigDecimal temp = new BigDecimal("0");
        BigDecimal size = new BigDecimal(Integer.toString(x.size()));

        for (int i = 0; i < x.size(); i++) {
            BigDecimal val = x.get(i).getScore();
            BigDecimal squrDiffToMean = val.subtract(mean, mc).pow(2);
            temp = temp.add(squrDiffToMean, mc);
        }

        BigDecimal meanOfDiffs = temp.divide(size, mc);
        return meanOfDiffs.sqrt(new MathContext(mathContext));
    }

    public static BigDecimal stdev(List<BigDecimal> x, int mathContext) {
        MathContext mc = new MathContext(7);
        BigDecimal mean = mean(x, 7);
        BigDecimal temp = new BigDecimal("0");
        BigDecimal size = new BigDecimal(Integer.toString(x.size()));

        for (int i = 0; i < x.size(); i++) {
            BigDecimal val = x.get(i);
            BigDecimal squrDiffToMean = val.subtract(mean, mc).pow(2);
            temp = temp.add(squrDiffToMean, mc);
        }

        BigDecimal meanOfDiffs = temp.divide(size, mc);
        return meanOfDiffs.sqrt(new MathContext(mathContext));
    }

    public static List removeDuplicateInList(List a) {
        LinkedHashSet hashSet = new LinkedHashSet<>(a);

        ArrayList listWithoutDuplicates = new ArrayList<>(hashSet);

        return listWithoutDuplicates;
    }
}
