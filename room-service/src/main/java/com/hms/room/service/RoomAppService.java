package com.hms.room.service;

import com.hms.common.dto.room.RoomDto;
import com.hms.common.dto.room.RoomImageDto;
import com.hms.room.entity.Room;
import com.hms.room.entity.RoomImage;
import com.hms.room.repository.RoomImageRepository;
import com.hms.room.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RoomAppService {

    private final RoomRepository roomRepo;
    private final RoomImageRepository imageRepo;

    public RoomAppService(RoomRepository roomRepo, RoomImageRepository imageRepo) {
        this.roomRepo = roomRepo;
        this.imageRepo = imageRepo;
    }

    public List<RoomDto> listRooms() {
        return roomRepo.findAll().stream().map(this::toDto).toList();
    }

    public RoomDto getRoom(Long id) {
        Room r = roomRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        return toDto(r);
    }

    public List<RoomImageDto> listImages(Long roomId) {
        return imageRepo.findByRoomIdOrderByCoverDescIdAsc(roomId).stream().map(this::toDto).toList();
    }

    @Transactional
    public RoomDto create(RoomDto dto) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is required");
        }
        if (roomRepo.existsByCode(dto.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room code already exists");
        }
        Room r = Room.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .type(dto.getType())
                .pricePerNight(dto.getPricePerNight())
                .status(dto.getStatus() == null ? "AVAILABLE" : dto.getStatus())
                .description(dto.getDescription())
                .build();
        roomRepo.save(r);
        return toDto(r);
    }

    @Transactional
    public RoomDto update(Long id, RoomDto dto) {
        Room r = roomRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        if (dto.getName() != null)
            r.setName(dto.getName());
        if (dto.getType() != null)
            r.setType(dto.getType());
        if (dto.getPricePerNight() != null)
            r.setPricePerNight(dto.getPricePerNight());
        if (dto.getStatus() != null)
            r.setStatus(dto.getStatus());
        if (dto.getDescription() != null)
            r.setDescription(dto.getDescription());
        return toDto(r);
    }

    @Transactional
    public void delete(Long id) {
        if (!roomRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        imageRepo.findByRoomIdOrderByCoverDescIdAsc(id).forEach(imageRepo::delete);
        roomRepo.deleteById(id);
    }

    @Transactional
    public RoomImageDto addImage(Long roomId, RoomImageDto dto) {
        roomRepo.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        if (dto.getUrl() == null || dto.getUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "url is required");
        }
        RoomImage img = RoomImage.builder()
                .roomId(roomId)
                .url(dto.getUrl())
                .cover(dto.isCover())
                .build();
        imageRepo.save(img);
        return toDto(img);
    }

    @Transactional
    public void deleteImage(Long roomId, Long imageId) {
        RoomImage img = imageRepo.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
        if (!img.getRoomId().equals(roomId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image does not belong to this room");
        }
        imageRepo.delete(img);
    }

    @Transactional
    public void setCover(Long roomId, Long imageId) {
        RoomImage newCover = imageRepo.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));
        if (!newCover.getRoomId().equals(roomId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image does not belong to this room");
        }
        
        // Remove cover flag from all images of this room
        List<RoomImage> roomImages = imageRepo.findByRoomIdOrderByCoverDescIdAsc(roomId);
        roomImages.forEach(img -> img.setCover(false));
        
        // Set new cover
        newCover.setCover(true);
        imageRepo.saveAll(roomImages);
    }

    private RoomDto toDto(Room r) {
        RoomDto dto = new RoomDto();
        dto.setId(r.getId());
        dto.setCode(r.getCode());
        dto.setName(r.getName());
        dto.setType(r.getType());
        dto.setPricePerNight(r.getPricePerNight());
        dto.setStatus(r.getStatus());
        dto.setDescription(r.getDescription());
        
        // Get cover image URL
        List<RoomImage> images = imageRepo.findByRoomIdOrderByCoverDescIdAsc(r.getId());
        if (!images.isEmpty()) {
            dto.setCoverImageUrl(images.get(0).getUrl()); // First image is cover (sorted by cover desc)
        }
        
        return dto;
    }

    private RoomImageDto toDto(RoomImage r) {
        RoomImageDto dto = new RoomImageDto();
        dto.setId(r.getId());
        dto.setRoomId(r.getRoomId());
        dto.setUrl(r.getUrl());
        dto.setCover(r.isCover());
        return dto;
    }
}
