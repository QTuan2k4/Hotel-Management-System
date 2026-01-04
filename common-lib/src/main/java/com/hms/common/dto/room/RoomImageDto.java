package com.hms.common.dto.room;

public class RoomImageDto {
    private Long id;
    private Long roomId;
    private String url;
    private boolean cover;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public boolean isCover() { return cover; }
    public void setCover(boolean cover) { this.cover = cover; }
}
