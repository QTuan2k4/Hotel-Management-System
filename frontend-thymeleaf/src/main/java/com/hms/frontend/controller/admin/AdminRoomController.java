package com.hms.frontend.controller.admin;

import com.hms.common.dto.room.RoomDto;
import com.hms.common.dto.room.RoomImageDto;
import com.hms.frontend.api.GatewayApiClient;
import com.hms.frontend.session.SessionAuth;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("auth", auth);
        return "admin/room-list";
    }

    @GetMapping("/new")
    public String createForm(Model model, HttpSession session) {
        requireAdmin(session);
        model.addAttribute("room", new RoomDto());
        model.addAttribute("mode", "create");
        model.addAttribute("images", List.of());
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
            model.addAttribute("images", List.of());
            return "admin/room-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        SessionAuth auth = requireAdmin(session);
        RoomDto room = api.get("/api/rooms/" + id, RoomDto.class, auth);
        RoomImageDto[] images = api.get("/api/admin/rooms/" + id + "/images", RoomImageDto[].class, auth);
        model.addAttribute("room", room);
        model.addAttribute("mode", "edit");
        model.addAttribute("images", images != null ? Arrays.asList(images) : List.of());
        return "admin/room-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("room") RoomDto room, HttpSession session,
            Model model) {
        SessionAuth auth = requireAdmin(session);
        try {
            api.put("/api/admin/rooms/" + id, room, RoomDto.class, auth);
            return "redirect:/admin/rooms/" + id + "/edit";
        } catch (Exception ex) {
            model.addAttribute("error", "Cập nhật thất bại: " + ex.getMessage());
            model.addAttribute("mode", "edit");
            RoomImageDto[] images = api.get("/api/admin/rooms/" + id + "/images", RoomImageDto[].class, auth);
            model.addAttribute("images", images != null ? Arrays.asList(images) : List.of());
            return "admin/room-form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        SessionAuth auth = requireAdmin(session);
        api.delete("/api/admin/rooms/" + id, auth);
        return "redirect:/admin/rooms";
    }

    // Image management endpoints
    @PostMapping("/{id}/images")
    public String addImage(@PathVariable Long id, @RequestParam String imageUrl, 
                           @RequestParam(defaultValue = "false") boolean cover,
                           HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = requireAdmin(session);
        try {
            RoomImageDto dto = new RoomImageDto();
            dto.setUrl(imageUrl);
            dto.setCover(cover);
            api.post("/api/admin/rooms/" + id + "/images", dto, RoomImageDto.class, auth);
            ra.addFlashAttribute("success", "Đã thêm ảnh thành công!");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Thêm ảnh thất bại: " + ex.getMessage());
        }
        return "redirect:/admin/rooms/" + id + "/edit";
    }

    @PostMapping("/{id}/images/upload")
    public String uploadImage(@PathVariable Long id, 
                              @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                              @RequestParam(defaultValue = "false") boolean cover,
                              HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = requireAdmin(session);
        try {
            // Upload file to room-service
            @SuppressWarnings("unchecked")
            java.util.Map<String, String> uploadResult = api.uploadFile("/api/upload/room-image", file, auth);
            
            if (uploadResult != null && uploadResult.containsKey("url")) {
                String imageUrl = uploadResult.get("url");
                
                // Add the image to the room
                RoomImageDto dto = new RoomImageDto();
                dto.setUrl(imageUrl);
                dto.setCover(cover);
                api.post("/api/admin/rooms/" + id + "/images", dto, RoomImageDto.class, auth);
                ra.addFlashAttribute("success", "Đã upload và thêm ảnh thành công!");
            } else {
                ra.addFlashAttribute("error", "Upload thất bại!");
            }
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Upload thất bại: " + ex.getMessage());
        }
        return "redirect:/admin/rooms/" + id + "/edit";
    }

    @PostMapping("/{roomId}/images/{imageId}/delete")
    public String deleteImage(@PathVariable Long roomId, @PathVariable Long imageId,
                              HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = requireAdmin(session);
        try {
            api.delete("/api/admin/rooms/" + roomId + "/images/" + imageId, auth);
            ra.addFlashAttribute("success", "Đã xóa ảnh!");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Xóa ảnh thất bại: " + ex.getMessage());
        }
        return "redirect:/admin/rooms/" + roomId + "/edit";
    }

    @PostMapping("/{roomId}/images/{imageId}/set-cover")
    public String setCover(@PathVariable Long roomId, @PathVariable Long imageId,
                           HttpSession session, RedirectAttributes ra) {
        SessionAuth auth = requireAdmin(session);
        try {
            api.postForStatus("/api/admin/rooms/" + roomId + "/images/" + imageId + "/set-cover", null, auth);
            ra.addFlashAttribute("success", "Đã đặt ảnh làm ảnh đại diện!");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Đặt ảnh đại diện thất bại: " + ex.getMessage());
        }
        return "redirect:/admin/rooms/" + roomId + "/edit";
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

