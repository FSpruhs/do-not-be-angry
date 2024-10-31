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
    private int rollCounter = 0;
    private boolean wasSix = false;

    public void start() {
        playingField = new PlayingField();
        for (Player player : players.players()) {
            playingField.putFiguresOnField(player.color());
        }
        int randomIndex = (int) (Math.random() * players.players().size());
        currentPlayer = players.players().get(randomIndex);

        while (playingField.winner().isEmpty()) {
            int roll = dice.roll();
            rollCounter++;
            if (roll == 6) {
                wasSix = true;
            }
            List<Action> possibleActions = playingField.possibleActions(currentPlayer, roll);
            if (possibleActions.isEmpty() && rollCounter <= 3 && !wasSix) {
                continue;
            } else if (possibleActions.isEmpty()) {
                wasSix = false;
                rollCounter = 0;
                Color nextColor = playingField.nextColor(currentPlayer.color());
                while (!players.containsColor(nextColor)) {
                    nextColor = playingField.nextColor(nextColor);
                }
                currentPlayer = players.getPlayerByColor(nextColor);
                continue;
            }
            Action action = currentPlayer.chooseAction(possibleActions, playingField);
            playingField.moveFigure(action);
            if (roll != 6) {
                wasSix = false;
                rollCounter = 0;
                Color nextColor = playingField.nextColor(currentPlayer.color());
                while (!players.containsColor(nextColor)) {
                    nextColor = playingField.nextColor(nextColor);
                }
                currentPlayer = players.getPlayerByColor(nextColor);
            }
        }
    }
}
