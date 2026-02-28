package com._1zko.hacknovahackathon26backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDB extends JpaRepository<UserDetails, Long> {
    UserDetails findByUserEmail(String email);
    UserDetails findByUsername(String userName);
    boolean existsByUsername(String userName);
    boolean existsByUserEmail(String userEmail);
}
