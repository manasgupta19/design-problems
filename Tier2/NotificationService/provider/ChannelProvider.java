package Tier2.NotificationService.provider;

import Tier2.NotificationService.model.ChannelType;

// ---------------------------------------------------------
// 2. CORE INTERFACES (Strategy Pattern)
// ---------------------------------------------------------
public interface ChannelProvider {
    boolean send(String userId, String content); // Returns true if success
    ChannelType getType();
}
