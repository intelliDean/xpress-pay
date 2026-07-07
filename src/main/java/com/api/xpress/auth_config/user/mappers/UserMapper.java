package com.api.xpress.auth_config.user.mappers;

import com.api.xpress.auth_config.user.data.dtos.UserDTO;
import com.api.xpress.auth_config.user.data.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

}
