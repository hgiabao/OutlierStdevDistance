package com.main;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {
    public static void main(String[] args) {
        //Zoogloeaceae.fa.out
        List<BigDecimal> bigDecimalList = new ArrayList<>();
        List<String> stringList = Arrays.asList("0.9429", "0.9439", "0.9422", "0.9663", "0.9584", "0.9341", "0.9503", "0.9570", "0.9497",
                "0.9386");

        for (String value : stringList) {
            bigDecimalList.add(new BigDecimal(value));
        }

        System.out.println(bigDecimalList);
        System.out.println(bigDecimalList.get(0).getClass());

        System.out.println(mean(bigDecimalList));
        System.out.println(stdev(bigDecimalList));

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
