package Tier1.DistributedCache;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import Tier1.DistributedCache.model.SegmentedLRUCache;
import Tier1.DistributedCache.router.DistributedCacheClient;

// ---------------------------------------------------------
// 5. DRIVER CLASS
// ---------------------------------------------------------
public class CacheDriver {
    public static void main(String[] args) {
        // Init Cluster: 3 Nodes
        List<SegmentedLRUCache<String, String>> cluster = new ArrayList<>();
        cluster.add(new SegmentedLRUCache<>(3)); // Node A (Cap 3)
        cluster.add(new SegmentedLRUCache<>(3)); // Node B (Cap 3)
        cluster.add(new SegmentedLRUCache<>(3)); // Node C (Cap 3)

        // Mock DB
        Function<String, String> mockDB = k -> "Value-" + k;

        DistributedCacheClient client = new DistributedCacheClient(cluster, mockDB);

        System.out.println("--- Scenario 1: Cache Aside (Miss then Hit) ---");
        client.get("user:1"); // Miss -> DB -> Cache
        client.get("user:1"); // Hit

        System.out.println("\n--- Scenario 2: Eviction (Capacity 3) ---");
        // Force routing to specific nodes or fill them up
        for(int i=0; i<10; i++) {
            client.get("data:" + i);
        }

        System.out.println("\n--- Scenario 3: Write Through ---");
        client.put("config:mode", "ACTIVE");
        client.get("config:mode"); // Should be Hit with new value
    }
}
