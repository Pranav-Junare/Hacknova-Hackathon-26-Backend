package com._1zko.hacknovahackathon26backend.service;

import com._1zko.hacknovahackathon26backend.repo.Question;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class JudgeService {

    public boolean runDsaTest(String userSubmittedCode, Question question) {
        // 1. STITCH: The user's class + the hidden main() from DB
        String finalSource = userSubmittedCode + "\n" + question.getTestWrapper();

        String fileName = "solution_" + UUID.randomUUID().toString().substring(0, 8);
        File cppFile = new File(fileName + ".cpp");
        File exeFile = new File(fileName + ".exe");

        try {
            // 2. SAVE: Write the combined code to a file
            Files.writeString(cppFile.toPath(), finalSource);

            // 3. COMPILE: Run g++ via CMD
            ProcessBuilder compileBuilder = new ProcessBuilder("cmd.exe", "/c", "g++ " + fileName + ".cpp -o " + fileName + ".exe");
            Process compileProcess = compileBuilder.start();
            compileProcess.waitFor(); // Wait for compilation to finish

            // Inside JudgeService.java, update the compileProcess block:

            if (compileProcess.exitValue() != 0) {
                System.out.println("❌ Compilation Error!");

                // 🔥 ADD THESE 4 LINES TO SEE THE ACTUAL C++ ERROR
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.out.println("   " + errorLine);
                }

                // Clean up the broken files so they don't pile up
                cppFile.delete();
                return false;
            }

            // 4. RUN: Execute the .exe and capture output
            ProcessBuilder runBuilder = new ProcessBuilder("cmd.exe", "/c", fileName + ".exe");
            Process runProcess = runBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            String output = reader.readLine(); // This reads "SUCCESS" or "FAIL"

            // 5. CLEANUP: Delete the files so they don't clutter your laptop
            cppFile.delete();
            exeFile.delete();

            // 6. COMPARE: Return true if the output matches what the DB expects
            return output != null && output.trim().equals(question.getExpectedOutput());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}