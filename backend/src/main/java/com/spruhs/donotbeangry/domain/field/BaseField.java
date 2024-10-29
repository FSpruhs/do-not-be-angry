package com.spruhs.donotbeangry.domain.field;

import com.spruhs.donotbeangry.domain.Color;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BaseField extends Field {
    private final Color color;
}
