package com.couch.potato.testservice.data.service;

import com.couch.potato.testservice.data.domain.User;
import com.couch.potato.testservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class User2UserDtoConverter implements Converter<User, UserDto> {

    private final ModelMapper modelMapper;

    @Override
    public UserDto convert(@NonNull User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
