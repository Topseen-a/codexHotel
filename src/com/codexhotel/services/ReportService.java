package com.codexhotel.services;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.enums.Role;
import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.models.Booking;
import com.codexhotel.data.models.Room;
import com.codexhotel.data.models.User;
import com.codexhotel.data.repositories.BookingRepository;
import com.codexhotel.data.repositories.RoomRepository;
import com.codexhotel.data.repositories.UserRepository;
import com.codexhotel.dtos.responses.ReportResponse;
import com.codexhotel.exceptions.AdminAccessRequiredException;
import com.codexhotel.exceptions.AdminNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public ReportResponse generateReport(String adminUserId) {
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found"));

        if (adminUser.getRole() != Role.ADMIN) {
            throw new AdminAccessRequiredException("Only admins can generate reports");
        }

        List<Room> allRooms = roomRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAll();

        int totalRooms = allRooms.size();
        int occupiedRooms = (int) allRooms.stream()
                .filter(room -> room.getStatus() == RoomStatus.OCCUPIED)
                .count();

        double totalRevenue = allBookings.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        ReportResponse response = new ReportResponse();
        response.setTotalRooms(totalRooms);
        response.setOccupiedRooms(occupiedRooms);
        response.setTotalRevenue(totalRevenue);

        return response;
    }
}