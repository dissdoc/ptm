package org.phototimemachine.security.provider;

import org.phototimemachine.domain.AppUser;
import org.phototimemachine.service.UserService;
import org.phototimemachine.service.validator.password.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class UserAuthenticationProvider implements AuthenticationProvider {

    final Logger logger = LoggerFactory.getLogger(UserAuthenticationProvider.class);

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication.getName().trim().length() <= 0 ||
                authentication.getCredentials().toString().trim().length() <= 0)
            throw new BadCredentialsException("Exists empty field!");

        AppUser user = userService.findByEmail(authentication.getName());
        if (user != null) {
            PasswordGenerator pg = new PasswordGenerator(user.getFirstName(), user.getLastName(),
                    user.getEmail(), authentication.getCredentials().toString());
            if (pg.getPassword().equals(user.getPassword())) {
                List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
                GrantedAuthority granted = new SimpleGrantedAuthority(user.getRole().getRoleId());
                auths.add(granted);
                return new UsernamePasswordAuthenticationToken(user.fullName(), user.getPassword(), auths);
            }
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
