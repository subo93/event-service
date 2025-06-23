package com.example.event_service.mapper;

import com.example.event_service.dto.UserDTO;
import com.example.event_service.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO dto);
}