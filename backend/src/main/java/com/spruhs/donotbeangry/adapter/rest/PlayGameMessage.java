package com.spruhs.donotbeangry.adapter.rest;

import com.spruhs.donotbeangry.domain.Color;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlayGameMessage(@NotNull List<Color> players) {
}
