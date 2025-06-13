package com.example.backend.auth;

import com.example.backend.auth.jwt.JwtUtil;
import com.example.backend.auth.jwt.TokensPair;
import com.example.backend.auth.mongodb.UserMongodbManager;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateful;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

@Stateful
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthService {
    @EJB
    private UserMongodbManager dbManager;

    private static final String PEPPER;

    static {
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(AuthService.class.getClassLoader().getResourceAsStream("config.properties"))) {
            properties.load(reader);
            PEPPER = properties.getProperty("PEPPER");
        } catch (IOException e){
            throw new RuntimeException("Error loading properties file");
        }
    }

    public TokensPair createUser(String username, String password) throws Exception {
        if (this.dbManager.getUserByUsername(username) != null) {
            return null;
        }
        String salt = this.generateSalt();
        byte[] hashPassword = this.hashPassword(password, salt);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(hashPassword);
        newUser.setSalt(salt);
        TokensPair newTokens = JwtUtil.generateTokensByUser(newUser);
        newUser.setRefreshToken(newTokens.refreshToken());
        this.dbManager.addUser(newUser);
        return newTokens;
    }

    public boolean checkUser(String username){
        try{
            User currUser = this.dbManager.getUserByUsername(username);
            if (currUser == null) {
                return false;
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public boolean deleteUser(String username) throws Exception{
        User currUser = this.dbManager.getUserByUsername(username);
        return this.dbManager.deleteUser(currUser);
    }

    public TokensPair refreshToken(String username) throws Exception{
        User currUser = this.dbManager.getUserByUsername(username);
        TokensPair newTokens = JwtUtil.generateTokensByUser(currUser);
        currUser.setRefreshToken(newTokens.refreshToken());
        this.dbManager.updateUser(currUser);
        return newTokens;
    }

    public TokensPair login(String username, String password) throws Exception{
        User currUser = this.dbManager.getUserByUsername(username);
        byte[] inpPassword = this.hashPassword(password, currUser.getSalt());
        if (Arrays.equals(inpPassword, currUser.getPassword())) {
            TokensPair newTokens = JwtUtil.generateTokensByUser(currUser);
            currUser.setRefreshToken(newTokens.refreshToken());
            this.dbManager.updateUser(currUser);
            return newTokens;
        } else {
            return null;
        }
    }

    public void logout(String username) throws Exception{
        User currUser = this.dbManager.getUserByUsername(username);
        currUser.setRefreshToken(null);
        this.dbManager.updateUser(currUser);
    }

    private String generateSalt() {
        return UUID.randomUUID().toString();
    }

    private byte[] hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        return digest.digest((salt + password + PEPPER).getBytes());
    }
}
