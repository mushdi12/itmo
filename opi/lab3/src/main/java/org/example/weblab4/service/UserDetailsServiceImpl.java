package org.example.weblab4.service;

import org.example.weblab4.jooq.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.example.weblab4.jooq.Tables.USERS;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final DSLContext dsl;

    @Autowired
    public UserDetailsServiceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersRecord user = dsl.selectFrom(USERS)
                .where(USERS.USERNAME.eq(username))
                .fetchOne();

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
} 