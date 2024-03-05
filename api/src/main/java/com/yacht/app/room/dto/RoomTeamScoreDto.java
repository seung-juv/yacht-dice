package com.yacht.app.room.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class RoomTeamScoreDto {

    @Getter
    @Setter
    public static class Response implements Serializable {
        private RoomTeamScoreUpperSectionDto.Response upperSection;
        private RoomTeamScoreLowerSectionDto.Response lowerSection;
    }
}
