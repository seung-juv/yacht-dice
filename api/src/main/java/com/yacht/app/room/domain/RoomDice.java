package com.yacht.app.room.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class RoomDice {
    private String id = UUID.randomUUID().toString();
    private Integer value;
    private Boolean isKeep = false;
    private Integer status = 0;
    private Position position = new Position();
    private Rotation rotation = new Rotation();

    @Getter
    @Setter
    public static class Position implements Serializable {
        private Double x = 0.0;
        private Double y = 0.0;
        private Double z = 0.0;
    }

    @Getter
    @Setter
    public static class Rotation implements Serializable {
        private Double x = 0.0;
        private Double y = 0.0;
        private Double z = 0.0;
    }
}
