package Tier1.NotificationService.provider;

import Tier1.NotificationService.model.ChannelType;

public class TwilioAdapter implements ChannelProvider {
    public ChannelType getType() { return ChannelType.SMS; }
    public boolean send(String userId, String content) {
        System.out.println("[Twilio] SMS sent to " + userId + ": " + content);
        return true;
    }
}
