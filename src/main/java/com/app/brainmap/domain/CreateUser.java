package com.app.brainmap.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUser {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private UserRoleType userRole;

}
