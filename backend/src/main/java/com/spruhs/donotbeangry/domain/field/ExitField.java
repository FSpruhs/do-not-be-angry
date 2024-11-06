package com.spruhs.donotbeangry.domain.field;

import com.spruhs.donotbeangry.domain.Color;
import lombok.*;

@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ExitField extends ColorfulField {

    private Field homeField;

    public ExitField(Color color) {
        super(color);
    }
}
