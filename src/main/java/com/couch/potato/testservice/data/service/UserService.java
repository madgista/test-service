package com.couch.potato.testservice.data.service;

import com.couch.potato.testservice.data.domain.User;
import com.couch.potato.testservice.data.repository.UserRepository;
import com.couch.potato.testservice.dto.UserDto;
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

    @NonNull
    public Optional<UserDto> findUserByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username).map(this::getUserDto);
    }

    @NonNull
    public UserDto load(@NonNull UserDto userDto) {
        var user = getOrThrowUser(userDto);
        var loaded = repository.save(user);
        return getOrThrowUserDto(loaded);
    }

    public int activate(@NonNull UserDto userDto) {
        var username = userDto.getUsername();
        return repository.activate(username);
    }

    public int deactivate(@NonNull UserDto userDto) {
        var username = userDto.getUsername();
        return repository.deactivate(username);
    }

    public int delete(@NonNull UserDto userDto) {
        var username = userDto.getUsername();
        return repository.deleteUserByUsernameIgnoreCase(username);
    }

    @Nullable
    private UserDto getUserDto(@NonNull User user) {
        var dto = conversionService.convert(user, UserDto.class);
        if (dto == null) {
            log.warn("Cannot convert a user with id -> {} to the DTO", user.getId());
        }
        return dto;
    }

    @NonNull
    private UserDto getOrThrowUserDto(@NonNull User user) {
        return Optional.ofNullable(conversionService.convert(user, UserDto.class))
            .orElseThrow(() -> new ConversionFailedException(
                TypeDescriptor.valueOf(User.class),
                TypeDescriptor.valueOf(UserDto.class),
                user.toString(),
                new NullPointerException("Cannot convert an entity object to the DTO one"))
            );
    }

    @NonNull
    private User getOrThrowUser(@NonNull UserDto userDto) {
        return Optional.ofNullable(conversionService.convert(userDto, User.class))
            .orElseThrow(() -> new IllegalArgumentException("Cannot convert a DTO object to the entity one -> %s".formatted(userDto)));
    }
}
