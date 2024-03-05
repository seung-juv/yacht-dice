package com.yacht.app.room.dto;

import com.yacht.app.room.domain.RoomDice;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoomDiceDtoMapper {
    RoomDiceDtoMapper INSTANCE = Mappers.getMapper(RoomDiceDtoMapper.class);

    void merge(RoomDiceDto.Request request, @MappingTarget RoomDice roomDice);

    RoomDice toEntity(RoomDiceDto.Request request);

    RoomDiceDto.Response toResponse(RoomDice roomDice);

}
