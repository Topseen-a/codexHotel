package com.codexhotel.model;

import com.codexhotel.enums.RoomStatus;
import com.codexhotel.enums.RoomType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Data
@Document(collection = "rooms")
public class Room {

    @Id
    private String id;

    @Indexed(unique = true)
    private int roomNumber;

    private RoomType type;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal basePrice;

    private RoomStatus status;
}
