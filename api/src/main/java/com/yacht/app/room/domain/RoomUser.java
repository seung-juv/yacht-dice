package com.yacht.app.room.domain;

import com.yacht.app.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RoomUser {
    private String id = UUID.randomUUID().toString();
    private String uuid;
    private String name;
    private User user;
    private Boolean isMaster = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime onlineAt = LocalDateTime.now();
    private RoomTeam roomTeam;
}
