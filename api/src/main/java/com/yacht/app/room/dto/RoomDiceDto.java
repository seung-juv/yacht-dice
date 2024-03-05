package com.yacht.app.room.dto;

import com.yacht.app.room.domain.RoomDice;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class RoomDiceDto {
    @Getter
    @Setter
    public static class Request implements Serializable {
        private String id;
        private Integer value;
        private RoomDice.Position position;
        private RoomDice.Rotation rotation;
    }

    @Getter
    @Setter
    public static class Response implements Serializable {
        private String id;
        private Integer value;
        private Boolean isKeep;
        private Integer status;
        private RoomDice.Position position;
        private RoomDice.Rotation rotation;
    }
}
