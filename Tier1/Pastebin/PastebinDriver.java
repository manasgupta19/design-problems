package Tier1.Pastebin;

import Tier1.Pastebin.exception.PasteNotFoundException;
import Tier1.Pastebin.exception.StorageLimitExceededException;
import Tier1.Pastebin.service.PastebinService;
import Tier1.Pastebin.service.PastebinServiceImpl;

// ---------------------------------------------------------
// 5. DRIVER CLASS (Simulation)
// ---------------------------------------------------------
class PastebinDriver {
    public static void main(String[] args) throws InterruptedException {
        PastebinService service = new PastebinServiceImpl();

        System.out.println("--- Scenario 1: Basic Create & Get ---");
        String key1 = service.createPaste("Hello System Design!", 5); // 5 seconds TTL
        System.out.println("Created Paste: " + key1);
        System.out.println("Retrieved Content: " + service.getPaste(key1));

        System.out.println("\n--- Scenario 2: Expiration (Lazy Delete) ---");
        System.out.println("Sleeping for 6 seconds...");
        Thread.sleep(6000); // Wait for expiration
        try {
            service.getPaste(key1);
        } catch (PasteNotFoundException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
        }

        System.out.println("\n--- Scenario 3: Storage Limit ---");
        try {
            // Generate 11KB string
            String largeContent = "A".repeat(1024 * 11);
            service.createPaste(largeContent, 60);
        } catch (StorageLimitExceededException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
        }
    }
}

