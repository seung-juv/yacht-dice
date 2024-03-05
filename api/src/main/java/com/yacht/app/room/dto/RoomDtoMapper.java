package com.yacht.app.room.dto;

import com.yacht.app.room.domain.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoomDtoMapper {

    RoomDtoMapper INSTANCE = Mappers.getMapper(RoomDtoMapper.class);

    Room toEntity(RoomDto.Create save);

    void merge(RoomDto.Update update, @MappingTarget Room room);

    RoomDto.Response toResponse(Room room);

    @Mapping(target = "users", source = "roomUsers")
    @Mapping(target = "teams", source = "roomTeams")
    @Mapping(target = "dices", source = "roomDices")
    @Mapping(target = "tempScore", source = "roomTeamTempScore")
    RoomDto.DetailResponse toDetailResponse(Room room);

    RoomDto.StatusResponse toStatusResponse(Room room);

    RoomDto.TurnResponse toTurnResponse(Room room);

    @Mapping(target = "dices", ignore = true)
    RoomDto.DiceRollResponse toDiceRollResponse(Room room);

    @Mapping(target = "dices", source = "roomDices")
    RoomDto.SetDiceValueResponse toSetDiceValueResponse(Room room);

    @Mapping(target = "teams", source = "roomTeams")
    RoomDto.SetScoreResponse toSetScoreResponse(Room room);

}
