package com.app.brainmap.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final String jwtSecret = "Hpxhzd2ipHmKblQozmCMeMbIwWK4Ggoxcczqa4jJgRbzt/7hTWiee35FtU5YyzxHi/D9tde4ul8CPcBUiIps3Q==";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        try{
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);

            String userId = decodedJWT.getSubject();
            String email = decodedJWT.getClaim("email").asString();

            JwtUserDetails userDetails = new JwtUserDetails(userId, email);

            return new JwtAuthenticationToken(userDetails, token,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        } catch (Exception e) {
            throw  new BadCredentialsException("Invalid JWT token:", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
