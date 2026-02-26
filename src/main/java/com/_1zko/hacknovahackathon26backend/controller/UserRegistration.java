package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRegistration {
    private final UserService userService;

    @PostMapping("/registerUser")
    public String userRegister(@RequestParam String userName,@RequestParam String nickName,@RequestParam String userEmail,@RequestParam String password){
        try{
            userService.userRegistrationAuth(userName,nickName,userEmail,password);

            return "Registration successful";
        }
        catch (IllegalStateException e){
            return e.getMessage();
        }
    }
}
