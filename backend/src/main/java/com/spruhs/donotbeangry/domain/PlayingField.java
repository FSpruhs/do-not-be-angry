package com.spruhs.donotbeangry.domain;

import com.spruhs.donotbeangry.domain.player.Player;

import java.util.List;
import java.util.Optional;

public interface PlayingField {
    Optional<Color> winner();
    List<Action> possibleActions(Player player, int roll);
    Color nextColor(Color color);
    void moveFigure(Action action);
    void reset();
}
