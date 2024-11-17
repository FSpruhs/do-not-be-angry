package com.spruhs.donotbeangry.adapter.rest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlayGameMessage(@NotNull List<PlayerMessage> players, @Min(1) int numberOfGames) {
}

