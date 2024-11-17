package com.spruhs.donotbeangry.adapter.rest;

import com.spruhs.donotbeangry.application.PlayGameUseCase;
import com.spruhs.donotbeangry.domain.Color;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("rest/v1/game")
@Validated
@RequiredArgsConstructor
public class GameRestAdapter {

    private final PlayGameUseCase playGameUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PlayGameResponse playGame(@Valid @RequestBody PlayGameMessage playGameMessage) {
        Map<Color, Integer> winners = playGameUseCase.playGame(new PlayGameUseCase.Command(
                playGameMessage.players().stream().map(PlayerMessage::toPlayer).toList(),
                playGameMessage.numberOfGames()
        ));
        return PlayGameResponse.fromWinners(winners);
    }
}
