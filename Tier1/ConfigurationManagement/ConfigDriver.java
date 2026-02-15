package Tier1.ConfigurationManagement;

import Tier1.ConfigurationManagement.service.DynamicConfigManager;

// ---------------------------------------------------------
// 5. DRIVER CLASS
// ---------------------------------------------------------
public class ConfigDriver {
    public static void main(String[] args) throws InterruptedException {
        // Init System
        DynamicConfigManager config = new DynamicConfigManager("./app.config", "OrderService");

        // 1. Subscribe to changes
        config.subscribe("rate.limit", limit ->
            System.out.println(">> ALERT: Rate Limit changed to: " + limit)
        );

        config.subscribe("feature.new_ui", enabled ->
            System.out.println(">> ALERT: New UI Feature toggled: " + enabled)
        );

        // 2. Access before update (Default)
        System.out.println("Current Limit: " + config.get("rate.limit", "50"));

        // Wait for Poller Updates (Simulation)
        Thread.sleep(4000);

        // 3. Access after updates
        System.out.println("Final Limit: " + config.get("rate.limit", "50"));
    }
}

