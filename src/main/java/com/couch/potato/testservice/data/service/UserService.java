package com.couch.potato.testservice.data.service;

import com.couch.potato.testservice.data.domain.User;
import com.couch.potato.testservice.data.repository.UserRepository;
import com.couch.potato.testservice.dto.UserDto;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final ConversionService conversionService;

    public Optional<UserDto> findUserByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username).map(this::getUserDto);
    }

    @Nullable
    private UserDto getUserDto(@NonNull User user) {
        var dto = conversionService.convert(user, UserDto.class);
        if (dto == null) {
            log.warn("Cannot convert a user with id -> {} to the DTO", user.getId());
        }
        return dto;
    }
}
