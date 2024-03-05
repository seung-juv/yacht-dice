package com.yacht.app.room.dto;

import com.yacht.app.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


public class RoomUserDto {


    @Getter
    @Setter
    public static class Response implements Serializable {
        private String id = UUID.randomUUID().toString();
        private String uuid;
        private String name;
        private User user;
        private Boolean isMaster = false;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime onlineAt = LocalDateTime.now();
        private RoomTeamDto.Response team;
    }
}
