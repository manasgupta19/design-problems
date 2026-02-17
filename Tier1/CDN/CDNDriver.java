package Tier1.CDN;

import Tier1.CDN.model.OriginServer;
import Tier1.CDN.model.Response;
import Tier1.CDN.service.EdgeServer;
import java.util.*;
import java.util.concurrent.*;


// 4. DRIVER CLASS
public class CDNDriver {
    public static void main(String[] args) throws InterruptedException {
        OriginServer origin = new OriginServer();
        EdgeServer edge = new EdgeServer(origin, 5000); // 5s TTL

        System.out.println("--- Scenario 1: Cold Start (Cache Miss) ---");
        Response r1 = edge.get("/home.jpg");
        System.out.println("User 1: " + r1.getData() + " [Hit: " + r1.isCacheHit() + "]");

        System.out.println("\n--- Scenario 2: Warm Cache (Cache Hit) ---");
        Response r2 = edge.get("/home.jpg");
        System.out.println("User 2: " + r2.getData() + " [Hit: " + r2.isCacheHit() + "]");

        System.out.println("\n--- Scenario 3: Thundering Herd (Coalescing) ---");
        // Simulate 5 concurrent users requesting NEW file
        ExecutorService pool = Executors.newFixedThreadPool(5);
        List<Callable<Response>> tasks = new ArrayList<>();
        for(int i=0; i<5; i++) {
            tasks.add(() -> edge.get("/viral_video.mp4"));
        }

        List<Future<Response>> results = pool.invokeAll(tasks);

        // Wait for all
        pool.shutdown();

        // Analysis: "Processing heavy fetch" should appear ONLY ONCE in logs
        for(Future<Response> f : results) {
            try { 
                System.out.println("User X got: " + f.get().getData()); 
            } catch(InterruptedException | ExecutionException e) {}
        }

        System.out.println("\n--- Scenario 4: Invalidation (Purge) ---");
        edge.purge("/home.jpg");
        Response r3 = edge.get("/home.jpg"); // Should hit origin again
        System.out.println("User 3 (After Purge): " + r3.getData() + " [Hit: " + r3.isCacheHit() + "]");
    }
}


