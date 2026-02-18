package Tier2.TaggingService.repository;

import java.util.List;

// ---------------------------------------------------------
// 2. STORAGE ENGINE ABSTRACTION (Simulating Cassandra + Elastic)
// ---------------------------------------------------------
public interface StorageEngine {
    void writeTags(String entityId, List<String> tags);
    List<String> readTags(String entityId);
    List<String> searchEntities(String tag);
}
