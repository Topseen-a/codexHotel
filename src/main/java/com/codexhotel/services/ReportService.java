package com.codexhotel.services;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.enums.Role;
import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.models.Booking;
import com.codexhotel.data.models.Payment;
import com.codexhotel.data.models.Room;
import com.codexhotel.data.models.User;
import com.codexhotel.data.repositories.BookingRepository;
import com.codexhotel.data.repositories.PaymentRepository;
import com.codexhotel.data.repositories.RoomRepository;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.responses.ReportResponse;
import com.codexhotel.exceptions.UnauthorizedActionException;
import com.codexhotel.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public ReportResponse generateReport(String requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (requester.getRole() != Role.ADMIN && requester.getRole() != Role.MANAGER) {
            throw new UnauthorizedActionException("Only admins or managers can generate reports");
        }

        List<Room> allRooms = roomRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAll();
        List<Payment> allPayments = paymentRepository.findAll();

        int totalRooms = allRooms.size();
        int occupiedRooms = (int) allRooms.stream()
                .filter(room -> room.getStatus() == RoomStatus.OCCUPIED)
                .count();
        int availableRooms = (int) allRooms.stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                .count();

        long activeBookings = allBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED)
                .count();

        double totalRevenue = allPayments.stream()
                .filter(Payment::isSuccessful)
                .mapToDouble(Payment::getAmount)
                .sum();

        ReportResponse response = new ReportResponse();
        response.setTotalRooms(totalRooms);
        response.setOccupiedRooms(occupiedRooms);
        response.setAvailableRooms(availableRooms);
        response.setActiveBookings(activeBookings);
        response.setTotalRevenue(totalRevenue);
        response.setOccupancyRate(totalRooms == 0 ? 0.0 : (occupiedRooms * 100.0) / totalRooms);

        return response;
    }
}
