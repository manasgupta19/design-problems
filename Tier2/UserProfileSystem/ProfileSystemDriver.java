package Tier2.UserProfileSystem;

import Tier2.UserProfileSystem.model.UserProfile;
import Tier2.UserProfileSystem.repository.ProfileDatabase;
import Tier2.UserProfileSystem.repository.RedisCache;
import Tier2.UserProfileSystem.service.ProfileService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 5. DRIVER CLASS
public class ProfileSystemDriver {
    public static void main(String[] args) throws InterruptedException {
        ProfileDatabase db = new ProfileDatabase();
        RedisCache l2 = new RedisCache();
        ProfileService service = new ProfileService(db, l2);

        System.out.println("--- Scenario 1: Cold Start (Cache Miss) ---");
        // Request coalescing test: 5 threads asking for "u1"
        ExecutorService pool = Executors.newFixedThreadPool(5);
        List<Callable<UserProfile>> tasks = new ArrayList<>();

        for(int i=0; i<5; i++) tasks.add(() -> service.get("u1"));

        pool.invokeAll(tasks); // All start roughly same time
        pool.shutdown();
        Thread.sleep(200); // Wait for logs

        System.out.println("\n--- Scenario 2: Warm Read (L1 Hit) ---");
        service.get("u1");

        System.out.println("\n--- Scenario 3: Update & Invalidate ---");
        service.update("u1", "Senior Architect");

        System.out.println("\n--- Scenario 4: Read After Update (Refetch) ---");
        service.get("u1");
    }
}

