package com.app.brainmap.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtUserDetails {

    private final String userId;
    private final String email;
}
