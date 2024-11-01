package com.spruhs.donotbeangry.domain.player;

import com.spruhs.donotbeangry.domain.Action;
import com.spruhs.donotbeangry.domain.Color;
import com.spruhs.donotbeangry.domain.PlayingField;
import com.spruhs.donotbeangry.domain.StandardPlayingField;

import java.util.List;

public record Player(Color color, Strategy strategy) {

    public Action chooseAction(List<Action> possibleActions, PlayingField playingField) {
        return strategy.chooseAction(possibleActions, playingField);
    }

    public Color color() {
        return color;
    }

}
