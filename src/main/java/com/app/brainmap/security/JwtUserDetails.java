package com.app.brainmap.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;


@Getter
@AllArgsConstructor
public class JwtUserDetails {

    private final UUID userId;
    private final String email;
}
