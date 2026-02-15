package Tier1.ConfigurationManagement.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

// ---------------------------------------------------------
// 1. THE CORE CONFIG MANAGER
// ---------------------------------------------------------
public class DynamicConfigManager {

    // In-memory cache for O(1) read access (No I/O on get)
    private final AtomicReference<Map<String, String>> configCache;

    // Callbacks for dynamic updates
    private final Map<String, CopyOnWriteArrayList<Consumer<String>>> listeners;

    private final String configFilePath;
    private final ConfigPoller poller; // Simulates the Sidecar/Network interaction

    public DynamicConfigManager(String path, String serviceName) {
        this.configFilePath = path;
        this.configCache = new AtomicReference<>(new ConcurrentHashMap<>());
        this.listeners = new ConcurrentHashMap<>();

        // 1. Load from Disk (Fail-Safe Recovery)
        loadFromDisk();

        // 2. Start Background Poller (Simulating Long-Poll/gRPC)
        this.poller = new ConfigPoller(serviceName, this::updateConfig);
        poller.start();
    }

    // ---------------------------------------------------------
    // 2. PUBLIC API
    // ---------------------------------------------------------
    public String get(String key, String defaultValue) {
        return configCache.get().getOrDefault(key, defaultValue);
    }

    public void subscribe(String key, Consumer<String> listener) {
        listeners.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    // ---------------------------------------------------------
    // 3. INTERNAL UPDATE LOGIC (Atomic Swap)
    // ---------------------------------------------------------
    // Called when Network/Sidecar receives new data
    private void updateConfig(Map<String, String> newConfig) {
        Map<String, String> oldConfig = configCache.get();

        // A. Atomic Memory Update
        configCache.set(newConfig);

        // B. Persistence (Fail-Safe)
        persistToDisk(newConfig);

        // C. Trigger Listeners (Delta Calculation)
        notifyListeners(oldConfig, newConfig);
    }

    private void persistToDisk(Map<String, String> config) {
        try {
            // Strategy: Write to temp, then atomic move [Source 1206]
            Path targetPath = Paths.get(configFilePath);
            Path tempPath = Paths.get(configFilePath + ".tmp");

            // Serialize (Mocking JSON conversion)
            String content = config.toString();
            Files.write(tempPath, content.getBytes(), StandardOpenOption.CREATE);

            // Atomic Move (POSIX guarantee)
            Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("[Disk] Config persisted safely.");
        } catch (IOException e) {
            System.err.println("[Error] Failed to persist config: " + e.getMessage());
        }
    }

    private void notifyListeners(Map<String, String> oldMap, Map<String, String> newMap) {
        // Only notify if value CHANGED
        for (String key : listeners.keySet()) {
            String oldVal = oldMap.get(key);
            String newVal = newMap.get(key);

            if (newVal != null && !newVal.equals(oldVal)) {
                for (Consumer<String> listener : listeners.get(key)) {
                    // Execute safely (catch user exceptions)
                    try { listener.accept(newVal); } catch (Exception ignored) {}
                }
            }
        }
    }

    // Load initial state on restart
    private void loadFromDisk() {
        // Implementation would read file and parse JSON into configCache
        // For simulation, we start empty or mock
        System.out.println("[Init] Loaded config from local disk.");
    }
}

