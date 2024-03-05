package com.yacht.app.room.dto;

import com.yacht.app.room.domain.RoomTeamScoreLowerSection;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoomTeamScoreLowerSectionDtoMapper {
    RoomTeamScoreLowerSectionDtoMapper INSTANCE = Mappers.getMapper(RoomTeamScoreLowerSectionDtoMapper.class);

    RoomTeamScoreLowerSectionDto.Response toResponse(RoomTeamScoreLowerSection roomTeamScoreLowerSection);

}
