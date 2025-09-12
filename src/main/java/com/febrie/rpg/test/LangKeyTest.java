package com.febrie.rpg.test;

import com.febrie.rpg.util.LangManager;
import java.io.FileWriter;
import java.io.IOException;

public class LangKeyTest {
    public static void main(String[] args) {
        LangManager langManager = LangManager.getInstance();
        
        // Initialize the language manager
        langManager.loadLanguages();
        
        // Validate all keys
        langManager.validateAllKeys();
        
        // Write results to debug.txt
        try (FileWriter writer = new FileWriter("src/main/resources/debug_test.txt")) {
            writer.write("Language Key Validation Test Results\n");
            writer.write("=====================================\n");
            writer.write("Test completed. Check console output for missing keys.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Test completed. Check console output for missing keys.");
    }
}