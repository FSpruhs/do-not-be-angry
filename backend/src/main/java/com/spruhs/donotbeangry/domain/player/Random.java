package com.spruhs.donotbeangry.domain.player;

import com.spruhs.donotbeangry.domain.Action;
import com.spruhs.donotbeangry.domain.PlayingField;

import java.util.List;

public class Random implements Strategy {

    @Override
    public Action chooseAction(List<Action> possibleActions, PlayingField playingField) {
        return possibleActions.get((int) (Math.random() * possibleActions.size()));
    }
}
