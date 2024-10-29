package com.spruhs.donotbeangry.domain;

public record Dice() {

    public int roll() {
        return (int) (Math.random() * 6) + 1;
    }

}
