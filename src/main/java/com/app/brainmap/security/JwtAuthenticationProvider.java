package com.app.brainmap.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);

            String subject = decodedJWT.getSubject();
            UUID userId = UUID.fromString(subject);
            String email = decodedJWT.getClaim("email").asString();

            String[] rolesArray = decodedJWT.getClaim("user_role").isNull() ? null : decodedJWT.getClaim("roles").asArray(String.class);
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            if (rolesArray != null) {
                for (String r : rolesArray) {
                    if (r == null || r.isBlank()) continue;
                    String roleName = r.startsWith("ROLE_") ? r : "ROLE_" + r.toUpperCase();
                    authorities.add(new SimpleGrantedAuthority(roleName));
                }
            }
            if (authorities.isEmpty()) {
                authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            }

            JwtUserDetails userDetails = new JwtUserDetails(userId, email);

            return new JwtAuthenticationToken(userDetails, token, authorities);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid JWT token", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}