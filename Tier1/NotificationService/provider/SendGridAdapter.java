package Tier1.NotificationService.provider;

import Tier1.NotificationService.model.ChannelType;

public class SendGridAdapter implements ChannelProvider {
    public ChannelType getType() { return ChannelType.EMAIL; }
    public boolean send(String userId, String content) {
        System.out.println("[SendGrid] Email sent to " + userId + ": " + content);
        return true;
    }
}
