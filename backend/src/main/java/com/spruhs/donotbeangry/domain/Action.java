package com.spruhs.donotbeangry.domain;

import com.spruhs.donotbeangry.domain.field.Field;

public record Action(Figure figure, Field start, Field target, int roll) {
}
