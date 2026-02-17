package Tier1.NotificationService.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import Tier1.NotificationService.model.ChannelType;
import Tier1.NotificationService.model.NotificationRequest;
import Tier1.NotificationService.model.Status;
import Tier1.NotificationService.provider.ChannelProvider;
import Tier1.NotificationService.provider.SendGridAdapter;
import Tier1.NotificationService.provider.TwilioAdapter;
import Tier1.NotificationService.ratelimiter.TokenBucket;

// ---------------------------------------------------------
// 5. NOTIFICATION SERVICE (The Orchestrator)
// ---------------------------------------------------------
public class NotificationPlatform {
    // In-memory queues simulating Kafka Topics
    private final Map<ChannelType, BlockingQueue<NotificationRequest>> queues = new ConcurrentHashMap<>();
    private final Map<ChannelType, ChannelProvider> providers = new HashMap<>();
    private final Map<ChannelType, TokenBucket> rateLimiters = new HashMap<>();

    // Status tracking (Simulating Cassandra)
    private final Map<String, Status> statusStore = new ConcurrentHashMap<>();

    // Thread pools for workers
    private final ExecutorService workerPool = Executors.newCachedThreadPool();

    public NotificationPlatform() {
        // Init Queues & Limiters
        for (ChannelType type : ChannelType.values()) {
            queues.put(type, new LinkedBlockingQueue<>());
            rateLimiters.put(type, new TokenBucket(5)); // 5 req/sec limit
        }

        // Init Providers
        registerProvider(new TwilioAdapter());
        registerProvider(new SendGridAdapter());

        // Start Workers
        startWorkers();
    }

    private void registerProvider(ChannelProvider p) {
        providers.put(p.getType(), p);
    }

    // API Endpoint
    public String send(NotificationRequest req) {
        String correlationId = UUID.randomUUID().toString();
        statusStore.put(correlationId, Status.QUEUED);

        // Fan-out Logic: Route to specific queues based on request
        for (ChannelType channel : req.getChannels()) {
            queues.get(channel).offer(req); // Enqueue to "Kafka"
        }

        System.out.println("Ack: Request Accepted " + correlationId);
        return correlationId;
    }

    private void startWorkers() {
        for (ChannelType type : ChannelType.values()) {
            workerPool.submit(() -> {
                BlockingQueue<NotificationRequest> queue = queues.get(type);
                ChannelProvider provider = providers.get(type);
                TokenBucket limiter = rateLimiters.get(type);

                while (true) {
                    try {
                        NotificationRequest req = queue.take(); // Consume message

                        // 1. Rate Limiting Check
                        if (limiter.tryConsume()) {
                            // 2. Send to Provider
                            boolean success = provider.send(req.getUserId(), req.getContent());
                            if (!success) {
                                // 3. Retry Logic (Simplified DLQ print)
                                System.err.println("Failed to send to " + type + ". Moving to DLQ.");
                            }
                        } else {
                            // Rate limit hit: Re-queue or backoff
                            System.out.println("Rate limit hit for " + type + ". Re-queueing.");
                            Thread.sleep(200);
                            queue.offer(req);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
            });
        }
    }
}
