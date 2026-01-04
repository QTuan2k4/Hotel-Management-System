package com.hms.room.controller;

import com.hms.common.dto.room.RoomDto;
import com.hms.common.dto.room.RoomImageDto;
import com.hms.room.security.GatewayHeaders;
import com.hms.room.service.RoomAppService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
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
    public RoomDto create(@RequestBody RoomDto dto, @RequestHeader HttpHeaders headers) {
        GatewayHeaders.requireAdmin(headers);
        return roomService.create(dto);
    }

    @PutMapping("/{id}")
    public RoomDto update(@PathVariable Long id, @RequestBody RoomDto dto, @RequestHeader HttpHeaders headers) {
        GatewayHeaders.requireAdmin(headers);
        return roomService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestHeader HttpHeaders headers) {
        GatewayHeaders.requireAdmin(headers);
        roomService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/images")
    public RoomImageDto addImage(@PathVariable Long id, @RequestBody RoomImageDto dto, @RequestHeader HttpHeaders headers) {
        GatewayHeaders.requireAdmin(headers);
        return roomService.addImage(id, dto);
    }
}
