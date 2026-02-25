package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.repo.UserDetails;
import com._1zko.hacknovahackathon26backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserLogin {

    private final UserService userService;

    @PostMapping("loginUser")
    public String userLogin(String email, String password, HttpSession session){
        try{
            UserDetails curUser=userService.userLoginAuth(email, password);
            session.setAttribute("currentUser", curUser);
            return "Successful login"+curUser.getNickName();
        }

        catch (IllegalStateException e){
            return e.getMessage();
        }
    }

}
