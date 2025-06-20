package com.example.backend.auth;

import dev.morphia.annotations.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Entity("users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private ObjectId id;
    @NotNull
    @Indexed(options = @IndexOptions(unique = true))
    private String username;
    @NotNull
    private byte[] password;
    @NotNull
    private String salt;
    @Property("refresh_token")
    private String refreshToken;
}


