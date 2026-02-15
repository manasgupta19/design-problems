package Tier1.ConfigurationManagement.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// ---------------------------------------------------------
// 4. MOCK POLLER (Simulating The Infrastructure Layer)
// ---------------------------------------------------------
public class ConfigPoller extends Thread {
    private final Consumer<Map<String, String>> onUpdate;

    public ConfigPoller(String service, Consumer<Map<String, String>> onUpdate) {
        this.onUpdate = onUpdate;
    }

    @Override
    public void run() {
        try {
            // Simulate network delay / Long Polling
            Thread.sleep(1000);

            System.out.println("[Network] Received Config Update v1");
            Map<String, String> update1 = new HashMap<>();
            update1.put("rate.limit", "100");
            update1.put("feature.new_ui", "false");
            onUpdate.accept(update1);

            Thread.sleep(2000);

            System.out.println("[Network] Received Config Update v2");
            Map<String, String> update2 = new HashMap<>();
            update2.put("rate.limit", "200"); // Changed
            update2.put("feature.new_ui", "true"); // Changed
            onUpdate.accept(update2);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}