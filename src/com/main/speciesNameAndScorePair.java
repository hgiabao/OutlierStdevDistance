package com.main;

import java.math.BigDecimal;

public class speciesNameAndScorePair {
    private final String name;
    private final BigDecimal score;

    public speciesNameAndScorePair(String n, BigDecimal s) {
        name = n;
        score = s;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getScore() {
        return score;
    }
}
