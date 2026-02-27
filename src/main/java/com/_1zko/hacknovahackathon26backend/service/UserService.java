package com._1zko.hacknovahackathon26backend.service;

import com._1zko.hacknovahackathon26backend.repo.UserDB;
import com._1zko.hacknovahackathon26backend.repo.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDB userDB;

    public UserDetails userLoginAuth(String email, String password){
        UserDetails userDetails=userDB.findByUserEmail(email);
        if(userDetails==null) throw new IllegalStateException("Email not found");

        if(!(userDetails.getPassword().equals(password))) throw new IllegalStateException("Wrong password");

        return userDetails;
    }

    public void userRegistrationAuth(UserDetails userDetails) {

        // 1. Validate the incoming data
        if(userDB.existsByUsername(userDetails.getUsername())) throw new IllegalStateException("Username already taken, use another");
        if (!isEmail(userDetails.getUserEmail())) throw new IllegalStateException("Invalid email");
        if(userDB.existsByUserEmail(userDetails.getUserEmail())) throw new IllegalStateException("Email already taken, login");
        if(userDetails.getPassword().length() <= 3) throw new IllegalStateException("Password should be greater than 3 characters");

        // 2. Set system defaults
        // (Pro-tip: 1200 is the standard mathematical starting point for Elo rating systems!)
        userDetails.setPoints(1000L);

        // 3. Save the fully assembled user to the database
        userDB.save(userDetails);
    }

    public boolean isEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.contains("@") && email.contains(".");
    }
}
