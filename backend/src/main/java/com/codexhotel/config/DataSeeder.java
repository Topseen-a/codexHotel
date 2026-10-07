package com.codexhotel.config;

import com.codexhotel.data.enums.RoomStatus;
import com.codexhotel.data.enums.RoomType;
import com.codexhotel.data.enums.Role;
import com.codexhotel.data.enums.Season;
import com.codexhotel.data.models.Pricing;
import com.codexhotel.data.models.Room;
import com.codexhotel.data.models.User;
import com.codexhotel.data.repositories.PricingRepository;
import com.codexhotel.data.repositories.RoomRepository;
import com.codexhotel.data.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Seeds the database with the minimum data needed for the API to actually be
 * usable on a fresh install:
 *
 * 1. A bootstrap ADMIN account — without this, nobody could ever become an
 *    admin, since staff creation (POST /api/users) itself requires an
 *    existing admin to call it. Only runs if no ADMIN exists yet.
 * 2. Pricing for every (RoomType, Season) combination — PricingService
 *    throws PricingNotFoundException for any combo that's missing one.
 * 3. A handful of sample rooms across all types, so bookings can be created
 *    immediately without going through room setup by hand first.
 *
 * Each item is checked individually before insert (not just "is the
 * collection non-empty"), so this self-heals if a previous run was
 * interrupted partway through — e.g. the app was killed mid-startup — and
 * only some of the expected rows made it in. Safe to leave running on every
 * startup, including in production.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PricingRepository pricingRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-email:admin@codexhotel.com}")
    private String adminEmail;

    @Value("${app.seed.admin-password:ChangeMe123!}")
    private String adminPassword;

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
        admin.setEmail(adminEmail);
        admin.setPhoneNumber("08000000000");
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(LocalDate.now());

        userRepository.save(admin);

        log.warn("Seeded bootstrap admin account [{}] — log in and change this password immediately.", adminEmail);
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
                pricing.setMultiplier(multiplierFor(season));
                pricingRepository.save(pricing);
                inserted++;
            }
        }

        if (inserted > 0) {
            log.info("Seeded {} missing pricing row(s).", inserted);
        }
    }

    private double multiplierFor(Season season) {
        return switch (season) {
            case WEEKDAY -> 1.0;
            case WEEKEND -> 1.3;
            case FESTIVE -> 1.6;
        };
    }

    private void seedRooms() {
        int inserted = 0;

        inserted += seedRoomIfMissing(101, RoomType.STANDARD, 5_000);
        inserted += seedRoomIfMissing(102, RoomType.STANDARD, 5_000);
        inserted += seedRoomIfMissing(201, RoomType.DELUXE, 10_000);
        inserted += seedRoomIfMissing(202, RoomType.DELUXE, 10_000);
        inserted += seedRoomIfMissing(301, RoomType.SUITE, 15_000);

        if (inserted > 0) {
            log.info("Seeded {} missing sample room(s).", inserted);
        }
    }

    private int seedRoomIfMissing(int number, RoomType type, double basePrice) {
        if (roomRepository.findByRoomNumber(number).isPresent()) {
            return 0;
        }

        Room room = new Room();
        room.setRoomNumber(number);
        room.setType(type);
        room.setBasePrice(basePrice);
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);
        return 1;
    }
}