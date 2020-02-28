package com.main;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class jackKnifeSingleFamily {

    public static void main(String[] args) {
        File file = new File("data/in/family_self/Acidaminococcaceae.fa.out");
        String outputFilePath = "data/out/singleFamilyJackKnife.txt";

        List<BigDecimal> scores = new ArrayList<>();
        List<String> speciesName = new ArrayList<>();
        List<speciesNameAndScorePair> Pair = new ArrayList<>();


        try {
            FileWriter fw = new FileWriter(outputFilePath);
            if (file.isFile()) {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;

                //read in each line, get species name and
                while ((line = br.readLine()) != null) {
                    String[] columns = line.split("\t");
                    speciesName.add(columns[4]);
                    scores.add(new BigDecimal(columns[columns.length - 1]));
                }
                fr.close();
                System.out.println(speciesName);
                System.out.println(scores);

                //pair species and scores together
                for (int i = 0; i < scores.size(); i++) {
                    Pair.add(new speciesNameAndScorePair(speciesName.get(i), scores.get(i)));
                }
                List<String> speciesNameNoDuplicate = removeDuplicateInList(speciesName);

//                for (int i = 0; i < Pair.size(); i++) {
//                    System.out.println(Pair.get(i).getName() + "\t" + Pair.get(i).getScore());
//                }

                //remove a species&score pair, then calculate mean & stdev
                for (String tempName : speciesNameNoDuplicate) {
                    List<speciesNameAndScorePair> tempPair = new ArrayList<>(Pair);

                    for (int j = 0; j < tempPair.size(); j++) {
                        if (tempPair.get(j).getName().equals(tempName)) {
                            tempPair.remove(j);
                        }
                    }
                    String str = "Excluded species: " + tempName + "\t" + mean(tempPair) + "\t" + stdev(tempPair) + "\n";
                    fw.write(str);
                }
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BigDecimal mean(List<speciesNameAndScorePair> x) {
        MathContext mc = new MathContext(20);
        BigDecimal sum = new BigDecimal("0");
        BigDecimal size = new BigDecimal(Integer.toString(x.size()));
        for (int i = 0; i < x.size(); i++) {
            sum = sum.add(x.get(i).getScore());
        }
        return sum.divide(size, new MathContext(4));
    }

    public static BigDecimal stdev(List<speciesNameAndScorePair> x) {
        MathContext mc = new MathContext(20);
        BigDecimal mean = mean(x);
        BigDecimal temp = new BigDecimal("0");
        BigDecimal size = new BigDecimal(Integer.toString(x.size()));

        for (int i = 0; i < x.size(); i++) {
            BigDecimal val = x.get(i).getScore();
            BigDecimal squrDiffToMean = val.subtract(mean, mc).pow(2);
            temp = temp.add(squrDiffToMean, mc);
        }

        BigDecimal meanOfDiffs = temp.divide(size, mc);
        return meanOfDiffs.sqrt(new MathContext(4));
    }

    public static List removeDuplicateInList(List a) {
        LinkedHashSet hashSet = new LinkedHashSet<>(a);

        ArrayList listWithoutDuplicates = new ArrayList<>(hashSet);

        return listWithoutDuplicates;
    }
}
