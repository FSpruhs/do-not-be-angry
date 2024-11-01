package com.spruhs.donotbeangry.domain;

import java.util.Random;

public record SimpleDice() implements Dice {

    public int roll() {
        final Random random = new Random();
        return random.nextInt(6) + 1;
    }

}
