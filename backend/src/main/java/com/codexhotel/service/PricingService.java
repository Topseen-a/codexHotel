package com.codexhotel.service;

import com.codexhotel.dto.response.PricingResponse;
import com.codexhotel.enums.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Validated
public interface PricingService {

    BigDecimal calculatePrice(@NotNull(message = "Room type cannot be empty") RoomType roomType,
                              @NotNull(message = "Base price cannot be empty")
                              @PositiveOrZero(message = "Base price cannot be negative") BigDecimal basePrice,
                              @NotNull(message = "Date cannot be empty") LocalDate date);

    List<PricingResponse> getPriceList();
}
