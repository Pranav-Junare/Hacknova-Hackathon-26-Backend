package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserRegistration {
    private final UserService userService;

    @PostMapping("/registerUser")
    public ResponseEntity<?> userRegister(@RequestParam String userName, @RequestParam String nickName, @RequestParam String userEmail, @RequestParam String password){
        try{
            userService.userRegistrationAuth(userName,nickName,userEmail,password);

            return ResponseEntity.ok(Map.of("message","Registration successful"));
        }
        catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",e.getMessage()));
        }
    }
}
