package Tier1.NotificationService.model;

import java.util.List;

public class NotificationRequest {
    String userId;
    String content;
    List<ChannelType> channels;
    String idempotencyKey;

    public NotificationRequest(String uid, String msg, List<ChannelType> ch, String key) {
        this.userId = uid; this.content = msg; this.channels = ch; this.idempotencyKey = key;
    }

    public String getUserId() { return userId; }
    public String getContent() { return content; }
    public List<ChannelType> getChannels() { return channels; }
    public String getIdempotencyKey() { return idempotencyKey; }
}
