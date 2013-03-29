package org.phototimemachine.security.provider;

import org.phototimemachine.domain.AppUser;
import org.phototimemachine.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = null;
        AppUser user = userService.findByEmail(username);
        if (user != null) {
            String password = user.getPassword();
            List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
            GrantedAuthority granted = new SimpleGrantedAuthority(user.getRole().getRoleId());
            auths.add(granted);

            userDetails = new User(username, password, auths);
        } else {
            throw new UsernameNotFoundException("User " + username + " not found!");
        }

        return userDetails;
    }
}
