package Tier2.TaggingService.model;

import java.util.List;

public class TagRequest {
    String entityId;
    List<String> tags;
    public TagRequest(String id, List<String> t) { this.entityId = id; this.tags = t; }

    public String getEntityId() { return entityId; }
    public List<String> getTags() { return tags; }
}
