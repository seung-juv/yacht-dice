package com.yacht.app.room.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomTeamScoreUpperSection implements Cloneable {
    private Integer aces;
    private Integer twos;
    private Integer threes;
    private Integer fours;
    private Integer fives;
    private Integer sixes;

    @Override
    public RoomTeamScoreUpperSection clone() throws CloneNotSupportedException {
        return (RoomTeamScoreUpperSection) super.clone();
    }
}
