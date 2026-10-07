package com.codexhotel.repository;

import com.codexhotel.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByBookingId(String bookingId);

    List<Payment> findBySuccessful(boolean successful);
}
