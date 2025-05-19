package org.example.weblab4.repository;

import org.example.weblab4.entity.User;
import org.example.weblab4.jooq.tables.records.UsersRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static org.example.weblab4.jooq.tables.Users.USERS;

@Repository
public class UserRepository {
    private final DSLContext dsl;

    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public User save(User user) {
        UsersRecord record = dsl.newRecord(USERS);
        record.setUsername(user.getUsername());
        record.setPassword(user.getPassword());
        record.store();
        user.setId(record.getId());
        return user;
    }

    public User findByUsername(String username) {
        UsersRecord record = dsl.selectFrom(USERS)
                .where(USERS.USERNAME.eq(username))
                .fetchOne();
        if (record == null) {
            return null;
        }
        return new User(record.getId(), record.getUsername(), record.getPassword());
    }

    public void delete(User user) {
        dsl.deleteFrom(USERS)
                .where(USERS.ID.eq(user.getId()))
                .execute();
    }
} 