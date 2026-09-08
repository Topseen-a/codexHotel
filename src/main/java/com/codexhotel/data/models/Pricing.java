package com.codexhotel.data.models;

import com.codexhotel.data.enums.RoomType;
import com.codexhotel.data.enums.Season;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "pricing")
public class Pricing {

    @Id
    private String id;
    private RoomType roomType;
    private double multiplier;
    private Season season;
}
