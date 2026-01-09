package com.hms.room.controller;

import com.hms.room.repository.RoomRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/rooms")
public class InternalRoomController {

    private final RoomRepository repo;

    public InternalRoomController(RoomRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/stats/count")
    public long countRooms() {
        return repo.count();
    }
}
