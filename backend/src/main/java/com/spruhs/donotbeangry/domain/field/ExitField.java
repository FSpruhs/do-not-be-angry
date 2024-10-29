package com.spruhs.donotbeangry.domain.field;

import com.spruhs.donotbeangry.domain.Color;
import lombok.*;

@RequiredArgsConstructor
@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ExitField extends Field {

    private final Color color;
    private Field homeField;

}
