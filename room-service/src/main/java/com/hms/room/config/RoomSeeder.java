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
        if (roomRepo.count() > 0)
            return;

        // Seed sample rooms
        Room r1 = roomRepo.save(Room.builder()
                .code("R101")
                .name("Deluxe Room")
                .type("DELUXE")
                .pricePerNight(new BigDecimal("150.00"))
                .status("AVAILABLE")
                .description("A luxurious deluxe room with city view")
                .build());

        Room r2 = roomRepo.save(Room.builder()
                .code("R102")
                .name("Standard Room")
                .type("STANDARD")
                .pricePerNight(new BigDecimal("100.00"))
                .status("AVAILABLE")
                .description("Comfortable standard room with modern amenities")
                .build());

        Room r3 = roomRepo.save(Room.builder()
                .code("R201")
                .name("Suite")
                .type("SUITE")
                .pricePerNight(new BigDecimal("300.00"))
                .status("AVAILABLE")
                .description("Premium suite with separate living area")
                .build());

        // Sample images
        imageRepo.save(RoomImage.builder()
                .roomId(r1.getId())
                .url("https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=800")
                .cover(true)
                .build());

        imageRepo.save(RoomImage.builder()
                .roomId(r2.getId())
                .url("https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800")
                .cover(true)
                .build());

        imageRepo.save(RoomImage.builder()
                .roomId(r3.getId())
                .url("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800")
                .cover(true)
                .build());

        System.out.println("Seeded 3 sample rooms");
    }
}
