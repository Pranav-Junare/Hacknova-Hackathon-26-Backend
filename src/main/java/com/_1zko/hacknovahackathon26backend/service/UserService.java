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

    public void userRegistrationAuth(String userName, String nickName, String userEmail, String password){

        UserDetails userDetails=new UserDetails();

        if(userDB.existsByUsername(userName)) throw new IllegalStateException("Username already taken, use another");
        if (!isEmail(userEmail)) throw new IllegalStateException("Invalid email");
        if(userDB.existsByUserEmail(userEmail)) throw new IllegalStateException("Email already taken, login");
        if(nickName.length()<=3) throw new IllegalStateException("nickname should be greater than 3");
        if(password.length()<=3) throw new IllegalStateException("password should be greater than 3");

        userDetails.setUsername(userName);
        userDetails.setUserEmail(userEmail);
        userDetails.setNickName(nickName);
        userDetails.setPassword(password);

        userDetails.setPoints(0L);

        userDB.save(userDetails);
    }

    public boolean isEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return email.contains("@") && email.contains(".");
    }
}
