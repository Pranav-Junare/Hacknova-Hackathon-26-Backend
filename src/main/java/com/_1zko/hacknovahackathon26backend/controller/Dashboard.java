package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.repo.UserDB;
import com._1zko.hacknovahackathon26backend.repo.UserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class Dashboard {

    // 1. Inject your database repository!
    private final UserDB userDB;

    @GetMapping("/dashboard")
    public ResponseEntity<?> Dash(HttpSession session){

        Object sesObj = session.getAttribute("currentUser");
        if(sesObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Not logged in"));
        }

        // 2. Get the stale user from the session just to know WHO is logged in
        UserDetails staleSessionUser = (UserDetails) sesObj;

        // 3. THE FIX: Go to MySQL and get their exact, up-to-the-second stats!
        UserDetails freshUser = userDB.findByUsername(staleSessionUser.getUsername());

        // (Optional but smart): Update the session so it holds the fresh data too
        session.setAttribute("currentUser", freshUser);

        // 4. Return the fresh points to React
        return ResponseEntity.ok(Map.of(
                "name", freshUser.getUsername(),
                "points", freshUser.getPoints()
        ));
    }
}