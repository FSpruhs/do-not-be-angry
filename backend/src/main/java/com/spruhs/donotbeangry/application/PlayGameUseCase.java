package com.spruhs.donotbeangry.application;

import com.spruhs.donotbeangry.domain.*;
import com.spruhs.donotbeangry.domain.player.Player;
import com.spruhs.donotbeangry.domain.player.Players;
import com.spruhs.donotbeangry.domain.player.Random;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class PlayGameUseCase {

    public Color playGame(Command command) {
        Dice dice = new SimpleDice();
        PlayingField playingField = new StandardPlayingField();
        List<Player> players = new LinkedList<>();
        for (Color color : command.players) {
            players.add(new Player(color, new Random()));
        }
        Game game = new Game(new Players(players), playingField, dice);
        return game.start();
    }

    public record Command(List<Color> players) {}
}