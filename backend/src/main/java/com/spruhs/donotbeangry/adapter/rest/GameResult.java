package com.spruhs.donotbeangry.adapter.rest;

import com.spruhs.donotbeangry.domain.Color;

public record GameResult(Color color, int totalWins, float winPercentage) { }
