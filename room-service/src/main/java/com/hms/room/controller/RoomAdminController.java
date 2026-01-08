package com.hms.room.controller;

import com.hms.common.dto.room.RoomDto;
import com.hms.common.dto.room.RoomImageDto;
import com.hms.room.service.RoomAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/rooms")
public class RoomAdminController {

    private final RoomAppService roomService;

    public RoomAdminController(RoomAppService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public RoomDto create(@RequestBody RoomDto dto) {
        return roomService.create(dto);
    }

    @PutMapping("/{id}")
    public RoomDto update(@PathVariable Long id, @RequestBody RoomDto dto) {
        return roomService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/images")
    public RoomImageDto addImage(@PathVariable Long id, @RequestBody RoomImageDto dto) {
        return roomService.addImage(id, dto);
    }
}
