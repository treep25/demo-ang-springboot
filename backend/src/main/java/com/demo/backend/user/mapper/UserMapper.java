package com.demo.backend.user.mapper;

import com.demo.backend.user.model.User;
import com.demo.backend.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    public User convertToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public List<UserDto> convertUsersToUserDtos(List<User> users) {
        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }
}
