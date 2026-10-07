package com.codexhotel.service.impl;

import com.codexhotel.dto.response.ReportResponse;
import com.codexhotel.enums.BookingStatus;
import com.codexhotel.enums.Role;
import com.codexhotel.enums.RoomStatus;
import com.codexhotel.model.Payment;
import com.codexhotel.model.Room;
import com.codexhotel.repository.BookingRepository;
import com.codexhotel.repository.PaymentRepository;
import com.codexhotel.repository.RoomRepository;
import com.codexhotel.security.AccessGuard;
import com.codexhotel.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository;
    private final AccessGuard accessGuard;

    @Override
    public ReportResponse generateReport(String requesterId) {
        accessGuard.requireRole(requesterId, "Only admins or managers can generate reports", Role.ADMIN, Role.MANAGER);

        List<Room> allRooms = roomRepository.findAll();

        int totalRooms = allRooms.size();
        int occupiedRooms = countRoomsWithStatus(allRooms, RoomStatus.OCCUPIED);
        int availableRooms = countRoomsWithStatus(allRooms, RoomStatus.AVAILABLE);

        long activeBookings = bookingRepository.findByStatus(BookingStatus.CONFIRMED).size();

        BigDecimal totalRevenue = paymentRepository.findBySuccessful(true)
                .stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportResponse.builder()
                .totalRooms(totalRooms)
                .occupiedRooms(occupiedRooms)
                .availableRooms(availableRooms)
                .activeBookings(activeBookings)
                .totalRevenue(totalRevenue)
                .occupancyRate(totalRooms == 0 ? 0.0 : (occupiedRooms * 100.0) / totalRooms)
                .build();
    }

    private int countRoomsWithStatus(List<Room> rooms, RoomStatus status) {
        return (int) rooms.stream()
                .filter(room -> room.getStatus() == status)
                .count();
    }
}
