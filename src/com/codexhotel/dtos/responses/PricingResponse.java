package com.codexhotel.dtos.responses;

import com.codexhotel.data.enums.RoomType;
import com.codexhotel.data.enums.Season;
import lombok.Data;

@Data
public class PricingResponse {

    private RoomType roomType;
    private Season season;
    private double price;
}
