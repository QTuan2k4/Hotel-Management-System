package com.hms.report.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "room-service", url = "${application.config.room-url}")
public interface RoomClient {

    @GetMapping("/api/internal/rooms/stats/count")
    long countTotalRooms();
}
