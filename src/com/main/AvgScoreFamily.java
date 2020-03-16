package com.main;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

//Output file syntax: <excluded species> <avg_rest_of_cluster> <stdev_rest_of_cluster> <avg_excluded_species> <#_of_stdev_away>
public class AvgScoreFamily {

    public static void main(String[] args) {
        File file = new File("data/in/family_self/Zoogloeaceae.fa.out");
        String outputFilePath = "data/out/singleFamilyJackKnife_Zoogloeaceae.txt";

        List<String> speciesName = new ArrayList<>();
        List<speciesNameAndScorePair> Pair = new ArrayList<>();


        try {
            FileWriter fw = new FileWriter(outputFilePath);
            if (file.isFile()) {
                List<BigDecimal> scores = new ArrayList<>();
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split("\t");
                    speciesName.add(columns[4]);
                    scores.add(new BigDecimal(columns[columns.length - 1]));
                }
                fr.close();
//                System.out.println(speciesName);
//                System.out.println(scores);

                //pair species and scores together
                for (int i = 0; i < scores.size(); i++) {
                    Pair.add(new speciesNameAndScorePair(speciesName.get(i), scores.get(i)));
                }

                List<String> speciesNameNoDuplicate = removeDuplicateInList(speciesName);

                //remove a species&score pair, then calculate mean & stdev
                for (String excludedSpecies : speciesNameNoDuplicate) {
                    List<speciesNameAndScorePair> tempListExcluded = new ArrayList<>();
                    List<speciesNameAndScorePair> tempList = new ArrayList<>(Pair);

                    int j = 0;
                    while (j < tempList.size()) {
                        if (tempList.get(j).getName().equals(excludedSpecies)) {
                            tempListExcluded.add(tempList.get(j));
                            tempList.remove(j);
                            j = 0;
                        }
                        else j++;
                    }

                    BigDecimal avgDiff = (mean(tempList,7).subtract(mean(tempListExcluded,7))).abs();
                    BigDecimal numOfStdev = avgDiff.divide(stdev(tempList,7), new MathContext(4));

                    String str = "Excluded species: " + excludedSpecies + "\t" + mean(tempList,4 ) + "\t" + stdev(tempList,4) + "\t"
                            + mean(tempListExcluded,4) + "\t" + numOfStdev + "\n";
                    fw.write(str);
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BigDecimal mean(List<speciesNameAndScorePair> x, int mathContext) {
        MathContext mc = new MathContext(20);
        BigDecimal sum = new BigDecimal("0");
        BigDecimal size = new BigDecimal(Integer.toString(x.size()));
        for (int i = 0; i < x.size(); i++) {
            sum = sum.add(x.get(i).getScore());
        }
        return sum.divide(size, new MathContext(mathContext));
    }

    public static BigDecimal stdev(List<speciesNameAndScorePair> x, int mathContext) {
        MathContext mc = new MathContext(20);
        BigDecimal mean = mean(x, 20);
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

    public static List removeDuplicateInList(List a) {
        LinkedHashSet hashSet = new LinkedHashSet<>(a);

        ArrayList listWithoutDuplicates = new ArrayList<>(hashSet);

        return listWithoutDuplicates;
    }
}
