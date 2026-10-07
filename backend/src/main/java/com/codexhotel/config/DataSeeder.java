package com.codexhotel.config;

import com.codexhotel.config.properties.SeedProperties;
import com.codexhotel.enums.RoomStatus;
import com.codexhotel.enums.RoomType;
import com.codexhotel.enums.Role;
import com.codexhotel.enums.Season;
import com.codexhotel.model.Pricing;
import com.codexhotel.model.Room;
import com.codexhotel.model.User;
import com.codexhotel.repository.PricingRepository;
import com.codexhotel.repository.RoomRepository;
import com.codexhotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PricingRepository pricingRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeedProperties seedProperties;

    @Override
    public void run(String... args) {
        seedBootstrapAdmin();
        seedPricing();
        seedRooms();
    }

    private void seedBootstrapAdmin() {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        User admin = new User();
        admin.setName("System Admin");
        admin.setEmail(seedProperties.adminEmail());
        admin.setPhoneNumber("08000000000");
        admin.setPassword(passwordEncoder.encode(seedProperties.adminPassword()));
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(LocalDate.now());

        userRepository.save(admin);

        log.warn("Seeded bootstrap admin account [{}] — log in and change this password immediately.", seedProperties.adminEmail());
    }

    private void seedPricing() {
        int inserted = 0;

        for (RoomType type : RoomType.values()) {
            for (Season season : Season.values()) {
                if (pricingRepository.findByRoomTypeAndSeason(type, season).isPresent()) {
                    continue;
                }

                Pricing pricing = new Pricing();
                pricing.setRoomType(type);
                pricing.setSeason(season);
                pricing.setMultiplier(season.getDefaultMultiplier());
                pricingRepository.save(pricing);
                inserted++;
            }
        }

        if (inserted > 0) {
            log.info("Seeded {} missing pricing row(s).", inserted);
        }
    }

    private void seedRooms() {
        int inserted = 0;

        inserted += seedRoomIfMissing(101, RoomType.STANDARD);
        inserted += seedRoomIfMissing(102, RoomType.STANDARD);
        inserted += seedRoomIfMissing(201, RoomType.DELUXE);
        inserted += seedRoomIfMissing(202, RoomType.DELUXE);
        inserted += seedRoomIfMissing(301, RoomType.SUITE);

        if (inserted > 0) {
            log.info("Seeded {} missing sample room(s).", inserted);
        }
    }

    private int seedRoomIfMissing(int number, RoomType type) {
        if (roomRepository.existsByRoomNumber(number)) {
            return 0;
        }

        Room room = new Room();
        room.setRoomNumber(number);
        room.setType(type);
        room.setBasePrice(type.getDefaultBasePrice());
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        return 1;
    }
}