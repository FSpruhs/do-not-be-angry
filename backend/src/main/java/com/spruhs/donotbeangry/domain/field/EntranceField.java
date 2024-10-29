package com.spruhs.donotbeangry.domain.field;

import com.spruhs.donotbeangry.domain.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EntranceField extends Field {

    private final Color color;
}
