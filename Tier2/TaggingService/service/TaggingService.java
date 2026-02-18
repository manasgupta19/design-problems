package Tier2.TaggingService.service;

import java.util.List;

import Tier2.TaggingService.model.TagRequest;
import Tier2.TaggingService.model.TagResponse;
import Tier2.TaggingService.repository.StorageEngine;

// ---------------------------------------------------------
// 3. TAGGING SERVICE (The Core Logic)
// ---------------------------------------------------------
public class TaggingService {
    private final StorageEngine storage;

    // Circuit Breaker / Rate Limiter simulation could go here

    public TaggingService(StorageEngine storage) {
        this.storage = storage;
    }

    // API: AddTags
    public TagResponse addTags(TagRequest req) {
        if (req.getEntityId() == null || req.getTags() == null || req.getTags().isEmpty()) {
            return new TagResponse(false, "Invalid Request");
        }

        try {
            // Principle: Write-Optimized. Just dump to storage.
            // Deduplication happens at the Set level in storage.
            storage.writeTags(req.getEntityId(), req.getTags());
            return new TagResponse(true, null);
        } catch (Exception e) {
            return new TagResponse(false, e.getMessage());
        }
    }

    // API: GetTags (Strong Consistency assumption for entity view)
    public List<String> getTags(String entityId) {
        return storage.readTags(entityId);
    }

    // API: SearchByTag (Eventual Consistency)
    public List<String> searchByTag(String tag) {
        return storage.searchEntities(tag);
    }
}

