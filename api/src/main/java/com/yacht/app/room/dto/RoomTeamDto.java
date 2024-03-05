package com.yacht.app.room.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class RoomTeamDto {

    @Getter
    @Setter
    public static class Response implements Serializable {
        private String id;
        private String name;
        private RoomTeamScoreDto.Response score;
    }
}
