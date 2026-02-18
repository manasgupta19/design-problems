package Tier2.TaggingService.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public // Mock Implementation using Concurrent Structures
class MockStorage implements StorageEngine {
    // Table 1: tags_by_entity (Simulates Cassandra)
    private final ConcurrentHashMap<String, Set<String>> entityStore = new ConcurrentHashMap<>();

    // Table 2: entities_by_tag (Simulates Elasticsearch/Inverted Index)
    private final ConcurrentHashMap<String, Set<String>> invertedIndex = new ConcurrentHashMap<>();

    @Override
    public void writeTags(String entityId, List<String> tags) {
        // 1. Write to Entity Store (Atomic compute)
        entityStore.compute(entityId, (key, existing) -> {
            if (existing == null) existing = ConcurrentHashMap.newKeySet();
            existing.addAll(tags);
            return existing;
        });

        // 2. Simulate Async CDC -> Update Inverted Index
        // In prod, this happens via Kafka consumer
        CompletableFuture.runAsync(() -> {
            for (String tag : tags) {
                invertedIndex.compute(tag, (k, v) -> {
                    if (v == null) v = ConcurrentHashMap.newKeySet();
                    v.add(entityId);
                    return v;
                });
            }
        });
    }

    @Override
    public List<String> readTags(String entityId) {
        Set<String> result = entityStore.get(entityId);
        return result == null ? Collections.emptyList() : new ArrayList<>(result);
    }

    @Override
    public List<String> searchEntities(String tag) {
        Set<String> result = invertedIndex.get(tag);
        return result == null ? Collections.emptyList() : new ArrayList<>(result);
    }
}
