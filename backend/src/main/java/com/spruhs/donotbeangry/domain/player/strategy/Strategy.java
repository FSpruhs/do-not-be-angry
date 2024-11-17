package com.spruhs.donotbeangry.domain.player.strategy;

import com.spruhs.donotbeangry.domain.Action;
import com.spruhs.donotbeangry.domain.PlayingField;

import java.util.List;

public interface Strategy {
    Action chooseAction(List<Action> possibleActions, PlayingField playingField);
}
