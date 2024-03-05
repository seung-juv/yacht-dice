package com.yacht.app.room.dto;

import com.yacht.app.room.domain.Room;
import com.yacht.app.user.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoomDto {
    @Getter
    @Setter
    public static class KeepDiceRequest implements Serializable {
        private String uuid;
        private String id;
    }

    @Getter
    @Setter
    public static class UnKeepDiceRequest implements Serializable {
        private String uuid;
        private String id;
    }

    @Getter
    @Setter
    public static class DiceRollRequest implements Serializable {
        private String uuid;
        private List<RoomDiceDto.Request> dices = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class DiceRollResponse implements Serializable {
        private String uuid;
        private Room.Status status;
        private Integer currentDiceRollCount;
        private List<RoomDiceDto.Request> dices = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class SetDiceValueRequest implements Serializable {
        private String uuid;
        private List<RoomDiceDto.Request> dices = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class SetDiceValueResponse implements Serializable {
        private String uuid;
        private List<RoomDiceDto.Response> dices = new ArrayList<>();
        private RoomTeamScoreDto.Response score;
    }

    @Getter
    @Setter
    public static class SetScoreRequest implements Serializable {
        private String uuid;
        private String scoreKey;
    }

    @Getter
    @Setter
    public static class SetScoreResponse implements Serializable {
        private List<RoomTeamDto.Response> teams = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class PlayRequest implements Serializable {
        private String uuid;
    }

    @Getter
    @Setter
    public static class Create implements Serializable {
        private Online user;
        private String title;
        private Boolean isSecret;
        private String secretPassword;
    }

    @Getter
    @Setter
    public static class StatusResponse implements Serializable {
        private Room.Status status;
    }

    @Getter
    @Setter
    public static class TurnResponse implements Serializable {
        private RoomTeamDto.Response progressRoomTeam;
        private Integer currentDiceRollCount;
    }

    @Getter
    @Setter
    public static class DetailRequest implements Serializable {
        private String password;
    }

    @Getter
    @Setter
    public static class Response implements Serializable {
        private String id;
        private Room.Status status;
        private String title;
        private Boolean isSecret;
        private UserDto.Response createdBy;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    public static class DetailResponse implements Serializable {
        private String id;
        private Room.Status status;
        private String title;
        private Boolean isSecret;
        private List<RoomDiceDto.Response> dices = new ArrayList<>();
        private UserDto.Response createdBy;
        private LocalDateTime createdAt;
        private List<RoomUserDto.Response> users;
        private List<RoomTeamDto.Response> teams = new ArrayList<>();
        private Integer currentDiceRollCount;
        private RoomTeamDto.Response progressRoomTeam;
        private RoomTeamScoreDto.Response tempScore;
    }

    @Getter
    @Setter
    public static class Update implements Serializable {
        private String title;
        private Boolean isSecret;
        private String secretPassword;
    }

    @Getter
    @Setter
    public static class DeleteAll implements Serializable {
        private List<String> ids;
    }

    @Getter
    @Setter
    public static class Online implements Serializable {
        private String uuid;
        private String name;
    }

    @Getter
    @Setter
    public static class Chat implements Serializable {
        private Long accountId;
        private String name;
        private String message;
    }

    @Getter
    @Setter
    public static class JoinTeamRequest implements Serializable {
        private String uuid;
        private String teamId;
    }

    @Getter
    @Setter
    public static class RemoveTeamRequest implements Serializable {
        private String uuid;
        private String teamId;
    }

}
