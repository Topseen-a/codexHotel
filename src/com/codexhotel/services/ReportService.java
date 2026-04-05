package com.codexhotel.services;

import com.codexhotel.data.enums.BookingStatus;
import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.models.Booking;
import com.codexhotel.data.models.Room;
import com.codexhotel.data.repositories.BookingRepository;
import com.codexhotel.data.repositories.RoomRepository;
import com.codexhotel.dtos.responses.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public ReportResponse generateReport() {

        List<Room> rooms = roomRepository.findAll();
        List<Booking> bookings = bookingRepository.findAll();

        int totalRooms = rooms.size();

        int occupiedRooms = (int) rooms.stream()
                .filter(room -> room.getStatus() == RoomStatus.OCCUPIED)
                .count();

        double totalRevenue = bookings.stream()
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
