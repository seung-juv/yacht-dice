package com.yacht.app.room.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomTeamScore implements Cloneable {
    private RoomTeamScoreUpperSection upperSection = new RoomTeamScoreUpperSection();
    private RoomTeamScoreLowerSection lowerSection = new RoomTeamScoreLowerSection();

    @Override
    public RoomTeamScore clone() throws CloneNotSupportedException {
        RoomTeamScore roomTeamScore = (RoomTeamScore) super.clone();
        roomTeamScore.setUpperSection(this.getUpperSection().clone());
        roomTeamScore.setLowerSection(this.getLowerSection().clone());
        return roomTeamScore;
    }
}
