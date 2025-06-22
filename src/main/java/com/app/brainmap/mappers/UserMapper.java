package com.app.brainmap.mappers;

import com.app.brainmap.domain.dto.UserDto;
import com.app.brainmap.domain.entities.User;

public interface UserMapper {
    User fromDto(UserDto userDto);

    UserDto toDto(User user);
}
