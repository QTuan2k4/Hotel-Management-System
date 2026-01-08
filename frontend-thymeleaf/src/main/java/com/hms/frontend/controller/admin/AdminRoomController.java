package com.hms.frontend.controller.admin;

import com.hms.common.dto.room.RoomDto;
import com.hms.frontend.api.GatewayApiClient;
import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/rooms")
public class AdminRoomController {

    private final GatewayApiClient api;

    public AdminRoomController(GatewayApiClient api) {
        this.api = api;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        SessionAuth auth = requireAdmin(session);
        RoomDto[] rooms = api.get("/api/rooms", RoomDto[].class, auth);
        model.addAttribute("rooms", rooms != null ? Arrays.asList(rooms) : List.of());
        return "admin/room-list";
    }

    @GetMapping("/new")
    public String createForm(Model model, HttpSession session) {
        requireAdmin(session);
        model.addAttribute("room", new RoomDto());
        model.addAttribute("mode", "create");
        return "admin/room-form";
    }

    @PostMapping
    public String create(@ModelAttribute("room") RoomDto room, HttpSession session, Model model) {
        SessionAuth auth = requireAdmin(session);
        try {
            api.post("/api/admin/rooms", room, RoomDto.class, auth);
            return "redirect:/admin/rooms";
        } catch (Exception ex) {
            model.addAttribute("error", "Tạo phòng thất bại: " + ex.getMessage());
            model.addAttribute("mode", "create");
            return "admin/room-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        SessionAuth auth = requireAdmin(session);
        RoomDto room = api.get("/api/rooms/" + id, RoomDto.class, auth);
        model.addAttribute("room", room);
        model.addAttribute("mode", "edit");
        return "admin/room-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("room") RoomDto room, HttpSession session,
            Model model) {
        SessionAuth auth = requireAdmin(session);
        try {
            api.put("/api/admin/rooms/" + id, room, RoomDto.class, auth);
            return "redirect:/admin/rooms";
        } catch (Exception ex) {
            model.addAttribute("error", "Cập nhật thất bại: " + ex.getMessage());
            model.addAttribute("mode", "edit");
            return "admin/room-form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        SessionAuth auth = requireAdmin(session);
        api.delete("/api/admin/rooms/" + id, auth);
        return "redirect:/admin/rooms";
    }

    private SessionAuth requireAdmin(HttpSession session) {
        Object v = session.getAttribute("AUTH");
        SessionAuth auth = (v instanceof SessionAuth a) ? a : new SessionAuth();
        if (!auth.isLoggedIn() || !auth.isAdmin()) {
            throw new RuntimeException("Admin login required. Please login with admin account.");
        }
        return auth;
    }
}
