package com.spruhs.donotbeangry.domain.player;

import java.util.List;

public record Players(List<Player> players) {

    public Players {
        if (players == null) {
            throw new IllegalArgumentException("Players cannot be null");
        }

        if (players.size() <= 1) {
            throw new IllegalArgumentException("Players must be at least 2");
        }

        if (players.size() > 4) {
            throw new IllegalArgumentException("Players must be at most 4");
        }

        if (players.stream().map(Player::color).distinct().count() != players.size()) {
            throw new IllegalArgumentException("Players must have unique colors");
        }

    }

}
