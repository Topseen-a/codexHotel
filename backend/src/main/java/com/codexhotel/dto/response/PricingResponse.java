package com.codexhotel.dto.response;

import com.codexhotel.enums.RoomType;
import com.codexhotel.enums.Season;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingResponse {

    private RoomType roomType;
    private Season season;
    private BigDecimal price;
}
