package com.yacht.app.room.dto;

import com.yacht.app.room.domain.RoomTeam;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoomTeamDtoMapper {
    RoomTeamDtoMapper INSTANCE = Mappers.getMapper(RoomTeamDtoMapper.class);

    RoomTeamDto.Response toResponse(RoomTeam roomTeam);

}
