package com.yacht.app.room.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class RoomTeamScoreUpperSectionDto {

    @Getter
    @Setter
    public static class Response implements Serializable {
        private Integer aces;
        private Integer twos;
        private Integer threes;
        private Integer fours;
        private Integer fives;
        private Integer sixes;
    }
}
