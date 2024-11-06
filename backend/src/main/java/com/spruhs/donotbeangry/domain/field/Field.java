package com.spruhs.donotbeangry.domain.field;

import com.spruhs.donotbeangry.domain.Figure;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "nextField")
@EqualsAndHashCode(exclude = "nextField")
public abstract class Field {
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

}
