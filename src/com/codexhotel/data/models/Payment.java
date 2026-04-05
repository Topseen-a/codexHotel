package com.codexhotel.data.models;

import com.codexhotel.data.enums.PaymentMethod;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;
    private String userId;
    private String bookingId;
    private double amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private boolean successful;
}
