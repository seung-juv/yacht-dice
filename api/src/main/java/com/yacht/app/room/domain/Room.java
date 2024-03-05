package com.yacht.app.room.domain;

import com.yacht.app.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Room {
    private String id = UUID.randomUUID().toString();
    private Integer diceLength = 5;
    private Status status;
    private String title;
    private Boolean isSecret = false;
    private String secretPassword;
    private User createdBy;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime deletedAt;
    private List<RoomChat> roomChats = new ArrayList<>();
    private List<RoomUser> roomUsers = new ArrayList<>();
    private List<RoomTeam> roomTeams = new ArrayList<>();

    private Integer currentDiceRollCount;
    private RoomTeam progressRoomTeam;
    private List<RoomDice> roomDices = new ArrayList<>();
    private RoomTeamScore roomTeamTempScore = new RoomTeamScore();
    private List<Integer> keepDiceIndexes = new ArrayList<>();

    public enum Status {
        READY,
        PLAYING
    }
}
