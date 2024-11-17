package com.spruhs.donotbeangry.application;

import com.spruhs.donotbeangry.domain.*;
import com.spruhs.donotbeangry.domain.player.Player;
import com.spruhs.donotbeangry.domain.player.Players;
import com.spruhs.donotbeangry.domain.player.strategy.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class PlayGameUseCase {

    public Map<Color, Integer> playGame(Command command) {

        Map<Color, Integer> result = new EnumMap<>(Color.class);

        Dice dice = new SimpleDice();
        List<Player> playerList = new LinkedList<>();
        for (Player player : command.players) {
            playerList.add(new Player(player.color(), new Random()));
        }
        Players players = new Players(playerList);
        PlayingField playingField = new StandardPlayingField(players);

        for (int i = 0; i < command.numberOfGames; i++) {
            Color winner = startGame(dice, players, playingField);
            log.info("Game {} won by {}", i + 1, winner);
            playingField.reset();
            result.put(winner, result.getOrDefault(winner, 0) + 1);
        }
        return result;
    }

    private Color startGame(Dice dice, Players players, PlayingField playingField) {
        Game game = new Game(players, playingField, dice);
        return game.start();
    }

    public record Command(List<Player> players, int numberOfGames) {
        public Command {
            if (players.size() < 2) {
                throw new IllegalArgumentException("At least two players are required");
            }
            if (numberOfGames < 1) {
                throw new IllegalArgumentException("At least one game is required");
            }
        }
    }
}