package com.hms.frontend.controller;

import com.hms.common.dto.room.RoomDto;
import com.hms.frontend.api.GatewayApiClient;
import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    private final GatewayApiClient api;

    public HomeController(GatewayApiClient api) {
        this.api = api;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session,
                       @RequestParam(required = false) String checkIn,
                       @RequestParam(required = false) String checkOut) {
        SessionAuth auth = getAuth(session);
        RoomDto[] rooms = api.get("/api/rooms", RoomDto[].class, auth);
        model.addAttribute("rooms", rooms != null ? Arrays.asList(rooms) : List.of());
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        return "index";
    }

    @GetMapping("/rooms/{id}")
    public String roomDetail(@PathVariable Long id, Model model, HttpSession session) {
        SessionAuth auth = getAuth(session);
        RoomDto room = api.get("/api/rooms/" + id, RoomDto.class, auth);
        model.addAttribute("room", room);

        // Images
        Object images = api.get("/api/rooms/" + id + "/images", Object.class, auth);
        model.addAttribute("images", images);
        return "room-detail";
    }

    private SessionAuth getAuth(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        return (v instanceof SessionAuth a) ? a : new SessionAuth();
    }
}
