package com.yacht.app.room.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomTeamScoreLowerSection implements Cloneable {
    private Integer threeOfAKind;
    private Integer fourOfAKind;
    private Integer fullHouse;
    private Integer smallStraight;
    private Integer largeStraight;
    private Integer chance;
    private Integer yahtzee;

    @Override
    public RoomTeamScoreLowerSection clone() throws CloneNotSupportedException {
        return (RoomTeamScoreLowerSection) super.clone();
    }
}
