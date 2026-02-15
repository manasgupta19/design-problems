package Tier1.ConfigurationManagement.service;

import java.util.function.Consumer;

public interface ConfigManager {
    /**
     * Non-blocking read from local state.
     * @param key The config key (e.g., "rate.limit.max")
     * @param defaultValue Fallback if key missing
     */
    String get(String key, String defaultValue);

    /**
     * Registers a callback for real-time updates.
     */
    void subscribe(String key, Consumer<String> listener);
}