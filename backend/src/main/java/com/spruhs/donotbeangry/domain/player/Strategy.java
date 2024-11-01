package com.spruhs.donotbeangry.domain.player;

import com.spruhs.donotbeangry.domain.Action;
import com.spruhs.donotbeangry.domain.PlayingField;
import com.spruhs.donotbeangry.domain.StandardPlayingField;

import java.util.List;

public interface Strategy {
    Action chooseAction(List<Action> possibleActions, PlayingField playingField);
}
