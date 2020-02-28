package com.main;

import java.util.Arrays;
import java.util.List;

public class test {
    public static void main(String[] args) {
        List<Double> scores = Arrays.asList(0.8664, 0.8518, 0.8796, 0.8925, 0.8486, 0.8509, 0.937, 0.8874, 0.8433, 0.8098, 0.8741, 0.8279, 0.8885, 0.8378, 0.8633);
        System.out.println(scores);
        System.out.println(mean(scores));

    }

    public static double mean(List<Double> x) {
        double sum = 0.0;
        for (int i = 0; i < x.size(); i++) {
            sum += x.get(i);
        }
        return sum / x.size();
    }

    public static double stdev(List<Double> x) {
        double mean = mean(x);
        double temp = 0;

        for (int i = 0; i < x.size(); i++) {
            double val = x.get(i);
            double squrDiffToMean = Math.pow(val - mean, 2);
            temp += squrDiffToMean;
        }

        double meanOfDiffs = temp / x.size();
        return Math.sqrt(meanOfDiffs);
    }
}
