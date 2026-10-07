package com.codexhotel.model;

import com.codexhotel.enums.PaymentMethod;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;
    private String userId;
    private String bookingId;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal amount;

    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private boolean successful;
}
