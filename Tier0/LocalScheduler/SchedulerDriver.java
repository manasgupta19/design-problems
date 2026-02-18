package Tier0.LocalScheduler;

import Tier0.LocalScheduler.service.PrincipalScheduler;

public class SchedulerDriver {
    public static void main(String[] args) throws InterruptedException {
        PrincipalScheduler scheduler = new PrincipalScheduler();
        long start = System.currentTimeMillis();

        Runnable logger = () -> {
            long now = System.currentTimeMillis();
            System.out.printf("[%4d ms] %s%n", (now - start), "Task executed");
        };

        System.out.println("--- Scenario 1: Basic Scheduling ---");
        // Task A: Run in 100ms
        scheduler.schedule(() -> logger.run(), 100);
        
        Thread.sleep(150); // Wait for A to finish

        System.out.println("\n--- Scenario 2: The 'Interruption' (Signal Logic) ---");
        // Step 1: Schedule a long-future task (Task B)
        System.out.println("Scheduling Task B (Due in 2000ms)...");
        scheduler.schedule(() -> logger.run(), 2000);
        
        // At this point, the Poller is sleeping for ~2000ms via awaitNanos().
        
        Thread.sleep(100); 
        
        // Step 2: Schedule a near-future task (Task C)
        // This MUST wake up the poller immediately via newEarlierTaskSignal.signal()
        System.out.println("Scheduling Task C (Due in 300ms)... [Should preempt B]");
        scheduler.schedule(() -> logger.run(), 300);

        // Verification: Wait to see if C runs before B
        Thread.sleep(2500);

        System.out.println("\n--- Scenario 3: Recurring Task ---");
        scheduler.scheduleAtFixedRate(() -> logger.run(), 0, 500);
        
        Thread.sleep(1600); // Should see ~3-4 heartbeats
        
        scheduler.stop();
    }
}
