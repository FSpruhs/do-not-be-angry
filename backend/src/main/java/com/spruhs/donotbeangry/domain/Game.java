package com.spruhs.donotbeangry.domain;

import com.spruhs.donotbeangry.domain.player.Player;
import com.spruhs.donotbeangry.domain.player.Players;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Game {

    private final Players players;
    private PlayingField playingField;
    private final Dice dice;
    private Player currentPlayer;

    public void start() {
        playingField = new PlayingField();
        for (Player player : players.players()) {
            playingField.putFiguresOnField(player.color());
        }
        int randomIndex = (int) (Math.random() * players.players().size());
        currentPlayer = players.players().get(randomIndex);

        while (playingField.winner().isEmpty()) {
            int roll = dice.roll();
            List<Action> possibleActions = playingField.possibleActions(currentPlayer, roll);

        }

    }








}
