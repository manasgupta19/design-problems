package Tier2.NotificationService.provider;

import Tier2.NotificationService.model.ChannelType;

public class SendGridAdapter implements ChannelProvider {
    public ChannelType getType() { return ChannelType.EMAIL; }
    public boolean send(String userId, String content) {
        System.out.println("[SendGrid] Email sent to " + userId + ": " + content);
        return true;
    }
}
