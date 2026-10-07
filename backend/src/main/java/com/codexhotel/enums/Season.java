package com.codexhotel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum Season {

    WEEKDAY(new BigDecimal("1.0")),
    WEEKEND(new BigDecimal("1.3")),
    FESTIVE(new BigDecimal("1.6"));

    private final BigDecimal defaultMultiplier;
}
