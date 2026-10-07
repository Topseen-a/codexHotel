package com.codexhotel.model;

import com.codexhotel.enums.RoomType;
import com.codexhotel.enums.Season;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Data
@CompoundIndex(name = "room_type_season", def = "{'roomType': 1, 'season': 1}", unique = true)
@Document(collection = "pricing")
public class Pricing {

    @Id
    private String id;
    private RoomType roomType;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal multiplier;

    private Season season;
}
