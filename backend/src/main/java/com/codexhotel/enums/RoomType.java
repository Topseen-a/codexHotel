package com.codexhotel.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum RoomType {

    STANDARD(new BigDecimal("5000")),
    DELUXE(new BigDecimal("10000")),
    SUITE(new BigDecimal("15000"));

    private final BigDecimal defaultBasePrice;
}
