package com._1zko.hacknovahackathon26backend.config;

import com._1zko.hacknovahackathon26backend.repo.Question;
import com._1zko.hacknovahackathon26backend.repo.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeed {

    @Bean
    public CommandLineRunner loadData(QuestionRepository questionRepo) {
        return args -> {
            if (questionRepo.count() == 0) {
                // --- DSA QUESTION 1: TWO SUM ---
                Question q1 = new Question();
                q1.setMode("dsa");
                q1.setTitle("1. Two Sum");
                q1.setDifficulty("Easy");
                q1.setDescription("Return indices of the two numbers such that they add up to target.");
                q1.setBoilerPlate("#include <iostream>\n#include <vector>\nusing namespace std;\n\nclass Solution {\npublic:\n    vector<int> twoSum(vector<int>& nums, int target) {\n        return {};\n    }\n};");
                q1.setExpectedOutput("SUCCESS");
                q1.setTestWrapper(
                        "\nint main() {\n" +
                                "    Solution sol;\n" +
                                "    vector<int> test1 = {2, 7, 11, 15};\n" + // Create it in memory first!
                                "    vector<int> test2 = {3, 2, 4};\n" +      // Create it in memory first!
                                "    if(sol.twoSum(test1, 9) == vector<int>{0,1} && sol.twoSum(test2, 6) == vector<int>{1,2}) {\n" +
                                "        cout << \"SUCCESS\";\n" +
                                "    } else { cout << \"FAIL\"; }\n" +
                                "    return 0;\n" +
                                "}"
                );
                questionRepo.save(q1);

                // --- DSA QUESTION 2: REVERSE STRING ---
                Question q2 = new Question();
                q2.setMode("dsa");
                q2.setTitle("2. Reverse String");
                q2.setDifficulty("Easy");
                q2.setDescription("Write a function that reverses a vector of characters in-place.");
                q2.setBoilerPlate("#include <iostream>\n#include <vector>\n#include <algorithm>\nusing namespace std;\n\nclass Solution {\npublic:\n    void reverseString(vector<char>& s) {\n        // Your code here\n    }\n};");
                q2.setExpectedOutput("SUCCESS");
                q2.setTestWrapper(
                        "\nint main() {\n" +
                                "    Solution sol;\n" +
                                "    vector<char> s = {'h','e','l','l','o'};\n" +
                                "    sol.reverseString(s);\n" +
                                "    if(s == vector<char>{'o','l','l','e','h'}) {\n" +
                                "        cout << \"SUCCESS\";\n" +
                                "    } else { cout << \"FAIL\"; }\n" +
                                "    return 0;\n" +
                                "}"
                );
                questionRepo.save(q2);

                // --- SYSTEM DESIGN QUESTION 1 ---
                Question q3 = new Question();
                q3.setMode("design");
                q3.setTitle("Mission: 10k RPS API");
                q3.setDifficulty("Medium");
                q3.setDescription("Include a Load Balancer, a Server, and a Database with valid connections.");
                // For design, boilerPlate can be empty as it uses React Flow
                q3.setBoilerPlate("");
                q3.setExpectedOutput("Load Balancer Server Database source");
                q3.setTestWrapper(""); // No wrapper needed for Design mode
                questionRepo.save(q3);

                System.out.println("✅ Database seeded with Judge-ready questions!");
            }
        };
    }
}