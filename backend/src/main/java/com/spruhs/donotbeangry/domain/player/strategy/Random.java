package com.spruhs.donotbeangry.domain.player.strategy;

import com.spruhs.donotbeangry.domain.Action;
import com.spruhs.donotbeangry.domain.PlayingField;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class Random implements Strategy {

    @Override
    public Action chooseAction(List<Action> possibleActions, PlayingField playingField) {
        return possibleActions.get((int) (Math.random() * possibleActions.size()));
    }
}
