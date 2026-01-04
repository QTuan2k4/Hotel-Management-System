package com.hms.room.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_images")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    private boolean cover;
}
