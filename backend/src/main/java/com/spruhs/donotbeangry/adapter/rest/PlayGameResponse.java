package com.spruhs.donotbeangry.adapter.rest;

import com.spruhs.donotbeangry.domain.Color;

import java.util.List;
import java.util.Map;

public record PlayGameResponse(int gamesPlayed, List<GameResult> result) {
    public static PlayGameResponse fromWinners(Map<Color, Integer> winners) {
        int gamesPlayed = winners.values().stream().mapToInt(Integer::intValue).sum();
        List<GameResult> result = winners.entrySet().stream()
                .map(entry -> new GameResult(entry.getKey(), entry.getValue(), (float) entry.getValue() / gamesPlayed))
                .toList();
        return new PlayGameResponse(gamesPlayed, result);
    }
}
