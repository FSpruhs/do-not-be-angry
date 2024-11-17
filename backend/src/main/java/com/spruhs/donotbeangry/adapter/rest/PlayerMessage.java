package com.spruhs.donotbeangry.adapter.rest;

import com.spruhs.donotbeangry.domain.Color;
import com.spruhs.donotbeangry.domain.player.Player;
import com.spruhs.donotbeangry.domain.player.strategy.HitFirst;
import com.spruhs.donotbeangry.domain.player.strategy.Random;
import com.spruhs.donotbeangry.domain.player.strategy.Strategy;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PlayerMessage(@NotNull Color color, @NotEmpty String strategy) {
    public static Player toPlayer(PlayerMessage playerMessage) {
        return new Player(playerMessage.color(), toStrategy(playerMessage.strategy()));
    }

    private static Strategy toStrategy(@NotEmpty String strategy) {
         switch (strategy) {
            case "random":
                return new Random();
             case "hitFirst":
                return new HitFirst();
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategy);
        }
    }
}
