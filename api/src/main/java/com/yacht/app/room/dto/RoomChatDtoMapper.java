package com.yacht.app.room.dto;

import com.yacht.app.room.domain.RoomChat;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoomChatDtoMapper {
    RoomChatDtoMapper INSTANCE = Mappers.getMapper(RoomChatDtoMapper.class);

    RoomChatDto.Response toResponse(RoomChat roomChat);


}
