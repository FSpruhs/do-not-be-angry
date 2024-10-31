package com.spruhs.donotbeangry.adapter.rest;

import com.spruhs.donotbeangry.application.PlayGameUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("rest/v1/game")
@Validated
@RequiredArgsConstructor
public class GameRestAdapter {

    private final PlayGameUseCase playGameUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PlayGameResponse playGame(@Valid @RequestBody PlayGameMessage playGameMessage) {
        playGameUseCase.playGame(new PlayGameUseCase.Command());
        return new PlayGameResponse();
    }
}
