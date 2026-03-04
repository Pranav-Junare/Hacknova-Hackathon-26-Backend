package com._1zko.hacknovahackathon26backend.repo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Simplified to 'id' for cleaner JPA Repo methods

    private String mode; // "dsa" or "design"
    private String title;
    private String difficulty;

    @Column(columnDefinition = "TEXT") // Allows long problem descriptions
    private String description;

    @Column(columnDefinition = "TEXT") // Allows full C++ boilerplate
    private String boilerPlate;

    private String expectedOutput; // Usually "SUCCESS"

    @Column(columnDefinition = "TEXT") // CRITICAL: Allows the full hidden main() function
    private String testWrapper;
}