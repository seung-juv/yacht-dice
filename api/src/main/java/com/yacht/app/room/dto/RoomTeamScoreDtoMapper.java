package com.yacht.app.room.dto;

import com.yacht.app.room.domain.RoomTeamScore;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoomTeamScoreDtoMapper {
    RoomTeamScoreDtoMapper INSTANCE = Mappers.getMapper(RoomTeamScoreDtoMapper.class);

    RoomTeamScoreDto.Response toResponse(RoomTeamScore roomTeamScore);

}
