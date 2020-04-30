package com.main;

import mdsj.MDSJ;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class MDSFromOutFile {

    private String str;

    public static void main(String[] args) {
        File file = new File("data/in/genus_self/Ruegeria.fa.out");
        String outputFilePath = "data/out/MDS/Ruegeria.csv";
        String outputFilePathMDS = "data/out/MDS/RuegeriaMDS.csv";
        List<String> speciesOne = new ArrayList<>();
        List<String> speciesTwo = new ArrayList<>();

        try {
            FileWriter fw = new FileWriter(outputFilePath);
            FileWriter fwMDS = new FileWriter(outputFilePathMDS);
            if (file.isFile()) {
                List<BigDecimal> scores = new ArrayList<>();
                String family = null;
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;

                while ((line = br.readLine()) != null) {
                    String[] columns = line.split("\t");
                    family = columns[3];
                    speciesOne.add(columns[4]);
                    speciesTwo.add(columns[9]);
                    scores.add(new BigDecimal(columns[columns.length - 1]));
                }
                fr.close();

                List<String> speciesNameNoDuplicate = removeDuplicateInList(speciesOne);
                speciesNameNoDuplicate.add(speciesTwo.get(speciesTwo.size() - 1));
                int size = speciesNameNoDuplicate.size();
                BigDecimal[][] matrix = new BigDecimal[size][size];

                //Make Upper Triangular Matrix
                int k = 0;
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        matrix[row][col] = row >= col ? BigDecimal.ONE : scores.get(k++);
                    }
                }

                //Upper Triangular to Full Matrix
                for (int i = 0; i < size; i++) {
                    for (int j = i + 1; j < size; j++) {
                        matrix[j][i] = matrix[i][j];
                    }
                }

                //Similarity to Dissimilarity(aka. Distance)
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        matrix[i][j] = methodB(matrix[i][j], 4);
                    }
                }

                //BigDecimal matrix to Double Matrix
                double[][] matrixDouble = new double[size][size];
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        matrixDouble[i][j] = matrix[i][j].doubleValue();
                    }
                }

                //MDS
                int n = matrix[0].length;    // number of data objects
                double[][] output = MDSJ.classicalScaling(matrixDouble); // apply MDS
                for (int i = 0; i < n; i++) {  // output all coordinates
                    String str = family + "\t"+speciesNameNoDuplicate.get(i) + "\t" + output[0][i] + "\t" + output[1][i] + "\n";
                    fwMDS.write(str);
                }

                //Wrint to file
                for (int i = 0; i < matrixDouble.length; i++) {
                    for (int j = 0; j < matrixDouble[i].length; j++) {
                        String str = matrixDouble[i][j] + "\t";
                        fw.write(str);
                    }
                    fw.write("\n");
                }
            }
            fwMDS.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List removeDuplicateInList(List a) {
        LinkedHashSet hashSet = new LinkedHashSet<>(a);

        ArrayList listWithoutDuplicates = new ArrayList<>(hashSet);

        return listWithoutDuplicates;
    }

    public static BigDecimal methodB(BigDecimal num, int mc) {
        BigDecimal res = (BigDecimal.ONE.divide(num, new MathContext(mc))).subtract(BigDecimal.ONE);
        return res;
    }
}
