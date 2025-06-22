package com.app.brainmap.mappers.impl;

import com.app.brainmap.domain.dto.UserDto;
import com.app.brainmap.domain.entities.User;
import com.app.brainmap.mappers.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User fromDto(UserDto userDto) {
        return new User(
                userDto.id(),
                userDto.firstName(),
                userDto.lastName(),
                userDto.username(),
                userDto.email(),
                userDto.mobileNumber()
        );
    }

    @Override
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getMobileNumber()
        );
    }
}
