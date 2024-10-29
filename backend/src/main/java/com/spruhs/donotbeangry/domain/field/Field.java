package com.spruhs.donotbeangry.domain.field;

import com.spruhs.donotbeangry.domain.Figure;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString(exclude = "nextField")
@EqualsAndHashCode(exclude = "nextField")
public class Field {
    private int id;
    private Field nextField;
    private Figure placedFigure;

    public boolean isEmpty() {
        return placedFigure == null;
    }

    public void placeFigure(Figure figure) {
        placedFigure = figure;
    }

    public void removeFigure() {
        placedFigure = null;
    }

    public Field getNextField() {
        return nextField;
    }

    public Figure getPlacedFigure() {
        return placedFigure;
    }

    public int getId() {
        return id;
    }
}
