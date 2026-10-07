package com.codexhotel.dto.response;

import com.codexhotel.enums.RoomStatus;
import com.codexhotel.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private String id;
    private int roomNumber;
    private RoomType type;
    private BigDecimal basePrice;
    private RoomStatus status;
}
