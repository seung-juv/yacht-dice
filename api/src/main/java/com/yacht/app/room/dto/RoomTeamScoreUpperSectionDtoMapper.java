package com.yacht.app.room.dto;

import com.yacht.app.room.domain.RoomTeamScoreUpperSection;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoomTeamScoreUpperSectionDtoMapper {
    RoomTeamScoreUpperSectionDtoMapper INSTANCE = Mappers.getMapper(RoomTeamScoreUpperSectionDtoMapper.class);

    RoomTeamScoreUpperSectionDto.Response toResponse(RoomTeamScoreUpperSection roomTeamScoreUpperSection);

}
