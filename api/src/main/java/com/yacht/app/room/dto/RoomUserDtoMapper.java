package com.yacht.app.room.dto;

import com.yacht.app.room.domain.RoomUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoomUserDtoMapper {
    RoomUserDtoMapper INSTANCE = Mappers.getMapper(RoomUserDtoMapper.class);

    @Mapping(target = "team", source = "roomTeam")
    RoomUserDto.Response toResponse(RoomUser roomUser);


}
