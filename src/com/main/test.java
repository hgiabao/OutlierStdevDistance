package com.main;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {
    public static void main(String[] args) {
        File file = new File("data/in/family_self/Zoogloeaceae.fa.out");
        String outputFilePath = "data/out/test.txt";

        //Zoogloeaceae.fa.out
        List<BigDecimal> bigDecimalList = new ArrayList<>();
//        List<String> stringList = Arrays.asList("0.9429", "0.9439", "0.9422", "0.9663", "0.9584", "0.9341", "0.9503", "0.9570", "0.9497",
//                "0.9386");


//        for (String value : stringList) {
//            bigDecimalList.add(new BigDecimal(value));
//        }

        try {
            FileWriter fw = new FileWriter(outputFilePath);
            if (file.isFile()) {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;

                // read in each line, add similarity score to List
                while ((line = br.readLine()) != null) {
                    String[] columns = line.split("\t");
                    bigDecimalList.add(new BigDecimal(columns[columns.length - 1]));
                }
                fr.close();

                //write to output
                String familyName = file.getName().substring(0, file.getName().indexOf("."));
                String str = familyName + "\t" + mean(bigDecimalList) + "\t" + stdev(bigDecimalList) + "\n";
                fw.write(str);
            }
            fw.close();
        } catch(IOException e){
            e.printStackTrace();
        }

        System.out.println(bigDecimalList);
//        System.out.println(mean(bigDecimalList));
//        System.out.println(stdev(bigDecimalList));

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
