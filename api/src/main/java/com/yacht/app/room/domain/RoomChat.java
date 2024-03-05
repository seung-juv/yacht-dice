package com.yacht.app.room.domain;

import com.yacht.app.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RoomChat {
    private String id = UUID.randomUUID().toString();
    private Room room;
    private String name;
    private String content;
    private User createdBy;
    private LocalDateTime createdAt = LocalDateTime.now();
}
