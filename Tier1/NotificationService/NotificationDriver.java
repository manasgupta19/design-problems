package Tier1.NotificationService;

import java.util.*;

import Tier1.NotificationService.model.ChannelType;
import Tier1.NotificationService.model.NotificationRequest;
import Tier1.NotificationService.service.NotificationPlatform;

// ---------------------------------------------------------
// 6. DRIVER CLASS
// ---------------------------------------------------------
class NotificationDriver {
    public static void main(String[] args) throws InterruptedException {
        NotificationPlatform platform = new NotificationPlatform();

        System.out.println("--- Scenario 1: Multi-Channel Fan-out ---");
        NotificationRequest req1 = new NotificationRequest(
            "User123",
            "Your OTP is 9999",
            Arrays.asList(ChannelType.SMS, ChannelType.EMAIL), // Fan-out
            "idempotency-key-1"
        );
        platform.send(req1);

        System.out.println("\n--- Scenario 2: Rate Limiting (Burst) ---");
        // Sending 10 SMS in loop. Bucket size is 5.
        for (int i = 0; i < 10; i++) {
            platform.send(new NotificationRequest("User"+i, "Promo", Collections.singletonList(ChannelType.SMS), "key-"+i));
        }

        Thread.sleep(2000); // Allow workers to process
        System.exit(0);
    }
}


