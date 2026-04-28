package Tier2.NotificationService.provider;

import Tier2.NotificationService.model.ChannelType;

public class TwilioAdapter implements ChannelProvider {
    public ChannelType getType() { return ChannelType.SMS; }
    public boolean send(String userId, String content) {
        System.out.println("[Twilio] SMS sent to " + userId + ": " + content);
        return true;
    }
}
