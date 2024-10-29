package com.spruhs.donotbeangry.domain.field;

import com.spruhs.donotbeangry.domain.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class HomeField extends Field {

    private final Color color;

    @Override
    public Field getNextField() {
        if (super.getNextField() != null && !super.getNextField().isEmpty()) {
            return null;
        }
        return super.getNextField();
    }
}
