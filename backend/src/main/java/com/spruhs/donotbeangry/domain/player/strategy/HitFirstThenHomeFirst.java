package com.spruhs.donotbeangry.domain.player.strategy;

import com.spruhs.donotbeangry.domain.Action;
import com.spruhs.donotbeangry.domain.PlayingField;
import com.spruhs.donotbeangry.domain.field.HomeField;

import java.util.List;

public class HitFirstThenHomeFirst implements Strategy {

    @Override
    public Action chooseAction(List<Action> possibleActions, PlayingField playingField) {
        for (Action action : possibleActions) {
            if (action.target().getPlacedFigure() != null) {
                return action;
            }
        }
        for (Action action : possibleActions) {
            if (action.target() instanceof HomeField) {
                return action;
            }
        }
        return possibleActions.get((int) (Math.random() * possibleActions.size()));
        }
}
