package com.yacht.app.room.dto;

import com.yacht.app.user.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class RoomChatDto {
    @Getter
    @Setter
    public static class Response implements Serializable {
        private UserDto.Response user;
        private String name;
        private String message;
    }
}
