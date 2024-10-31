package com.spruhs.donotbeangry.application;

import org.springframework.stereotype.Service;

@Service
public class PlayGameUseCase {

    public void playGame(Command command) {
        System.out.println("Playing game");
    }

    public record Command() {}
}