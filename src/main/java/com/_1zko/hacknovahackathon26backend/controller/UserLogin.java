package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.repo.UserDetails;
import com._1zko.hacknovahackathon26backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserLogin {

    private final UserService userService;

    @PostMapping("/loginUser")
    public ResponseEntity<?> userLogin(@RequestBody Map<String, String> payload, HttpSession session){
        try{
            String email=payload.get("email");
            String password=payload.get("password");
            UserDetails curUser=userService.userLoginAuth(email, password);
            session.setAttribute("currentUser", curUser);
            return ResponseEntity.ok(Map.of("message","Successfully loged in"));
        }

        catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",e.getMessage()));
        }
    }

}
