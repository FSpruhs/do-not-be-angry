package com.spruhs.donotbeangry.domain.player.strategy;

import com.spruhs.donotbeangry.domain.Action;
import com.spruhs.donotbeangry.domain.PlayingField;

import java.util.List;

public class HitFirst implements Strategy{
    @Override
    public Action chooseAction(List<Action> possibleActions, PlayingField playingField) {
        for (Action action : possibleActions) {
            if (action.target().getPlacedFigure() != null) {
                return action;
            }
        }
        return possibleActions.get((int) (Math.random() * possibleActions.size()));
    }
}
