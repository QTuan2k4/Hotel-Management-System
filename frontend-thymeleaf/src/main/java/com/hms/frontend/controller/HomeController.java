package com.hms.frontend.controller;

import com.hms.common.dto.room.RoomDto;
import com.hms.frontend.api.GatewayApiClient;
import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    private final GatewayApiClient api;

    public HomeController(GatewayApiClient api) {
        this.api = api;
    }

    @GetMapping("/")
    public String home(@org.springframework.web.bind.annotation.RequestParam(required = false) String query,
                       @org.springframework.web.bind.annotation.RequestParam(required = false) String type,
                       @org.springframework.web.bind.annotation.RequestParam(required = false) Double minPrice,
                       @org.springframework.web.bind.annotation.RequestParam(required = false) Double maxPrice,
                       Model model, HttpSession session) {
        SessionAuth auth = getAuth(session);
        
        // Build query string
        StringBuilder url = new StringBuilder("/api/rooms?status=AVAILABLE");
        if (query != null && !query.isBlank()) url.append("&query=").append(query);
        if (type != null && !type.isBlank()) url.append("&type=").append(type);
        if (minPrice != null) url.append("&minPrice=").append(minPrice);
        if (maxPrice != null) url.append("&maxPrice=").append(maxPrice);
        
        RoomDto[] roomsArr = api.get(url.toString(), RoomDto[].class, auth);
        List<RoomDto> rooms = roomsArr == null ? List.of() : Arrays.asList(roomsArr);
        
        // Fetch room types for filter dropdown
        String[] typesArr = api.get("/api/rooms/types", String[].class, auth);
        List<String> types = typesArr == null ? List.of() : Arrays.asList(typesArr);
        
        model.addAttribute("rooms", rooms);
        model.addAttribute("roomTypes", types);
        // Pass filter values back to view
        model.addAttribute("query", query);
        model.addAttribute("selectedType", type);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        
        return "home";
    }

    @GetMapping("/rooms/{id}")
    public String roomDetail(@PathVariable Long id, Model model, HttpSession session) {
        SessionAuth auth = getAuth(session);
        RoomDto room = api.get("/api/rooms/" + id, RoomDto.class, auth);
        model.addAttribute("room", room);
        Object images = api.get("/api/rooms/" + id + "/images", Object.class, auth);
        model.addAttribute("images", images);
        return "rooms/detail";
    }

    private SessionAuth getAuth(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        return (v instanceof SessionAuth a) ? a : new SessionAuth();
    }
}
