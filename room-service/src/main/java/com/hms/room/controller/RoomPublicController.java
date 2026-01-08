package com.hms.room.controller;

import com.hms.common.dto.room.RoomDto;
import com.hms.common.dto.room.RoomImageDto;
import com.hms.room.service.RoomAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomPublicController {

    private final RoomAppService roomService;

    public RoomPublicController(RoomAppService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomDto> list() {
        return roomService.listRooms();
    }

    @GetMapping("/{id}")
    public RoomDto get(@PathVariable Long id) {
        return roomService.getRoom(id);
    }

    @GetMapping("/{id}/images")
    public List<RoomImageDto> images(@PathVariable Long id) {
        return roomService.listImages(id);
    }
}
