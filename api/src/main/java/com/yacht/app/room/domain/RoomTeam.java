package com.yacht.app.room.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RoomTeam {
    private String id = UUID.randomUUID().toString();
    private String name;
    private LocalDateTime createdAt = LocalDateTime.now();
    private List<RoomUser> roomUsers = new ArrayList<>();
    private RoomTeamScore score = new RoomTeamScore();
}
