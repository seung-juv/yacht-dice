package com.yacht.app.room.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class RoomTeamScoreLowerSectionDto {

    @Getter
    @Setter
    public static class Response implements Serializable {
        private Integer threeOfAKind;
        private Integer fourOfAKind;
        private Integer fullHouse;
        private Integer smallStraight;
        private Integer largeStraight;
        private Integer chance;
        private Integer yahtzee;
    }
}
