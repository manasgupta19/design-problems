package Tier0.LocalScheduler.service;

public interface LocalScheduler {
    // One-time execution
    void schedule(Runnable task, long delayMs);

    // Recurring execution (Cron-style interval)
    void scheduleAtFixedRate(Runnable task, long initialDelay, long period);

    // Lifecycle management
    void stop();
}
