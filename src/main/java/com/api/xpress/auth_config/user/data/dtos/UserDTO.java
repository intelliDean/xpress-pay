package com.api.xpress.auth_config.user.data.dtos;

import com.api.xpress.auth_config.user.data.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record UserDTO (

    String fullName,

    String emailAddress,

    Set<Role> roles,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEEE, d MMMM, yyyy hh:mm:ssa")
    LocalDateTime registeredAt,

    boolean enabled
){}
