package com.yacht.app.user.dto;

import com.yacht.app.user.domain.Account;
import com.yacht.app.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserDtoMapper {
    UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    UserDto.Response toResponse(Account account);

    UserDto.Response toResponse(User user);

}
