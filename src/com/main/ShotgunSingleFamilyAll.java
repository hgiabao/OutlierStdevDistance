package com.main;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

//Output file syntax: <excluded_species> <avg_excluded_species> <avg_rest_of_family>  <stdev_rest_of_family> <#_of_stdev_away>
public class ShotgunSingleFamilyAll {

    private String str;

    public static void main(String[] args) {
        String inputDirPath = "data/in/genus_self/";
        String outputFilePath = "data/out/genus/";
        File folder = new File(inputDirPath);
        File[] FilesList = folder.listFiles();

        try {
            for (File file : FilesList) {
                if (getFileExtension(file).equals(".out")) {
                    List<String> speciesName = new ArrayList<>();
                    List<speciesNameAndScorePair> Pair = new ArrayList<>();
                    String fileName = file.getName().substring(0, file.getName().indexOf("."));
                    FileWriter fw = new FileWriter(outputFilePath + fileName + ".csv");
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

                        //pair species and scores together
                        for (int i = 0; i < scores.size(); i++) {
                            Pair.add(new speciesNameAndScorePair(speciesName.get(i), scores.get(i)));
                        }

                        List<String> speciesNameNoDuplicate = removeDuplicateInList(speciesName);

                        //Separet potential oulier, then calculate mean & stdev
                        for (String Species : speciesNameNoDuplicate) {
                            //Separate potential outlier and rest of cluster into 2 lists
                            List<speciesNameAndScorePair> tempList = new ArrayList<>();
                            List<speciesNameAndScorePair> tempListExcluded = new ArrayList<>();
                            int j = 0;
                            while (j < Pair.size()) {
                                if (Pair.get(j).getName().equals(Species)) {
                                    tempListExcluded.add(Pair.get(j));
                                } else tempList.add(Pair.get(j));
                                j++;
                            }

                            //Get avg similarity for rest of cluster
                            List<BigDecimal> othersAvgs = new ArrayList<>();
                            for (String s : speciesNameNoDuplicate) {
                                List<speciesNameAndScorePair> tempList1 = new ArrayList<>();
                                int k = 0;
                                while (k < tempList.size()) {
                                    if (tempList.get(k).getName().equals(s)) {
                                        tempList1.add(tempList.get(k));
                                    }
                                    k++;
                                }

                                if (tempList1.size() > 0) {
                                    BigDecimal speciesAvg = meanPair(tempList1, 4);
                                    othersAvgs.add(speciesAvg);
                                }
                            }

/*                        BigDecimal speciesAvg = meanPair(tempListExcluded, 4);
                        BigDecimal othersAvg = mean(othersAvgs, 4);
                        BigDecimal othersStdev = stdev(othersAvgs, 4);
                        BigDecimal numOfStdevAway;
                        String str;
                        if (othersStdev.compareTo(BigDecimal.ZERO) == 0) {
                            str = Species + "\t" + speciesAvg + "\t" + othersAvg + "\t" + othersStdev + "\t" + "#DIV/0" + "\n";
                        } else {
                            numOfStdevAway = (speciesAvg.subtract(othersAvg)).abs().divide(othersStdev, new MathContext(4));
                            str = Species + "\t" + speciesAvg + "\t" + othersAvg + "\t" + othersStdev + "\t" + numOfStdevAway + "\n";
                        }*/

                            BigDecimal speciesAvg;
                            BigDecimal othersAvg = null;
                            BigDecimal othersStdev = null;
                            BigDecimal numOfStdevAway = null;
                            int a = 1, b = 1;
                            String str = null;

                            speciesAvg = meanPair(tempListExcluded, 4);
                            if (othersAvgs.size() == 0) {
                                a = 0;
                            } else {
                                othersAvg = mean(othersAvgs, 4);
                                othersStdev = stdev(othersAvgs, 4);
                                if (othersStdev.compareTo(BigDecimal.ZERO) == 0) {
                                    b = 0;
                                } else {
                                    numOfStdevAway = (speciesAvg.subtract(othersAvg)).abs().divide(othersStdev, new MathContext(4));
                                }
                            }

                            switch (a) {
                                case 0:
                                    str = Species + "\t" + speciesAvg + "\t" + "#DIV/0" + "\t" + "#DIV/0" + "\t" + "#DIV/0" + "\n";
                                    break;
                                case 1:
                                    switch (b) {
                                        case 0:
                                            str = Species + "\t" + speciesAvg + "\t" + othersAvg + "\t" + othersStdev + "\t" + "#DIV/0" + "\n";
                                            break;
                                        case 1:
                                            str = Species + "\t" + speciesAvg + "\t" + othersAvg + "\t" + othersStdev + "\t" + numOfStdevAway + "\n";
                                            break;
                                    }
                                    break;
                            }

                            fw.write(str);
                        }
                    }
                    fw.close();
                }
            }

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


    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }


}
