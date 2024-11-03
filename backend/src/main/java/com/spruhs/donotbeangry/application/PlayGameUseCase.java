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
        List<Player> playerList = new LinkedList<>();
        for (Color color : command.players) {
            playerList.add(new Player(color, new Random()));
        }

        Players players = new Players(playerList);
        PlayingField playingField = new StandardPlayingField(players);
        Game game = new Game(players, playingField, dice);
        return game.start();
    }

    public record Command(List<Color> players) {}
}