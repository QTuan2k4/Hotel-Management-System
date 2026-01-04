package com.hms.room.config;

import com.hms.room.entity.Room;
import com.hms.room.entity.RoomImage;
import com.hms.room.repository.RoomImageRepository;
import com.hms.room.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RoomSeeder implements CommandLineRunner {

    private final RoomRepository roomRepo;
    private final RoomImageRepository imageRepo;

    public RoomSeeder(RoomRepository roomRepo, RoomImageRepository imageRepo) {
        this.roomRepo = roomRepo;
        this.imageRepo = imageRepo;
    }

    @Override
    public void run(String... args) {
        if (roomRepo.count() > 0) return;

        Room r1 = roomRepo.save(Room.builder()
                .code("R101")
                .name("Phòng Standard - 1 Giường")
                .type("STANDARD")
                .pricePerNight(new BigDecimal("500000"))
                .status("AVAILABLE")
                .description("Phòng tiêu chuẩn cho 2 người, có điều hòa và wifi.")
                .build());

        Room r2 = roomRepo.save(Room.builder()
                .code("R201")
                .name("Phòng Deluxe - View thành phố")
                .type("DELUXE")
                .pricePerNight(new BigDecimal("850000"))
                .status("AVAILABLE")
                .description("Phòng deluxe rộng rãi, view thành phố.")
                .build());

        imageRepo.save(RoomImage.builder().roomId(r1.getId()).url("https://picsum.photos/seed/r101/800/400").cover(true).build());
        imageRepo.save(RoomImage.builder().roomId(r2.getId()).url("https://picsum.photos/seed/r201/800/400").cover(true).build());
    }
}
