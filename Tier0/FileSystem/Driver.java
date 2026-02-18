package Tier0.FileSystem;

import Tier0.FileSystem.service.PrincipalFileSystem;

public class Driver {
     // ---------------------------------------------------------
    // 5. DRIVER / DRY RUN
    // ---------------------------------------------------------
    public static void main(String[] args) {
        PrincipalFileSystem fs = new PrincipalFileSystem();

        // Scenario 1: Create Structure
        System.out.println("1. Creating /usr directory...");
        fs.mkdir("/usr");

        // Scenario 2: Write File (Fragmentation Test)
        // Block size is 4. "HelloWorld" is 10 bytes -> Needs 3 blocks.
        System.out.println("2. Writing /usr/hello.txt...");
        fs.writeFile("/usr/hello.txt", "HelloWorld");

        // Scenario 3: Read Verification
        String content = fs.readFile("/usr/hello.txt");
        System.out.println("3. Read content: " + content);

        // Scenario 4: Update File (Overwrite)
        System.out.println("4. Overwriting with 'Hi'...");
        fs.writeFile("/usr/hello.txt", "Hi");
        System.out.println("5. New content: " + fs.readFile("/usr/hello.txt"));

        fs.mkdir("/usr/bin");
        fs.mkdir("/var");
        fs.writeFile("/usr/notes.txt", "Content");
        fs.writeFile("/var/log.txt", "LogData");

        System.out.println("--- Scenario 6: List Root Directory ---");
        // Should show [usr, var]
        System.out.println("ls /      -> " + fs.ls("/"));

        System.out.println("--- Scenario 7: List Nested Directory ---");
        // Should show [bin, notes.txt]
        System.out.println("ls /usr   -> " + fs.ls("/usr"));

        System.out.println("--- Scenario 8: List File (Target Specific) ---");
        // Should show [notes.txt]
        System.out.println("ls /usr/notes.txt -> " + fs.ls("/usr/notes.txt"));

        System.out.println("--- Scenario 9: Error Handling ---");
        try {
            fs.ls("/tmp"); // Does not exist
        } catch (RuntimeException e) {
            System.out.println("ls /tmp   -> Error: " + e.getMessage());
        }
    }

}
