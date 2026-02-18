package Tier2.TaggingService;

import java.util.*;
import java.util.concurrent.*;
import Tier2.TaggingService.model.TagRequest;
import Tier2.TaggingService.repository.MockStorage;
import Tier2.TaggingService.service.TaggingService;

// ---------------------------------------------------------
// 4. DRIVER CLASS
// ---------------------------------------------------------
public class Driver {
    public static void main(String[] args) throws InterruptedException {
        // Init
        MockStorage storage = new MockStorage();
        TaggingService service = new TaggingService(storage);

        System.out.println("--- Scenario 1: Basic Tagging ---");
        service.addTags(new TagRequest("video_1", Arrays.asList("funny", "viral")));
        service.addTags(new TagRequest("video_2", Arrays.asList("viral", "news")));

        System.out.println("Tags for video_1: " + service.getTags("video_1"));

        // Wait for Async Indexer (CDC simulation)
        Thread.sleep(100);

        System.out.println("\n--- Scenario 2: Search (Inverted Index) ---");
        List<String> viralVideos = service.searchByTag("viral");
        System.out.println("Entities tagged 'viral': " + viralVideos);

        System.out.println("\n--- Scenario 3: Idempotency (Duplicate Add) ---");
        // Adding same tag again shouldn't duplicate in result
        service.addTags(new TagRequest("video_1", Arrays.asList("funny")));
        System.out.println("Tags for video_1 (after duplicate add): " + service.getTags("video_1"));

        System.out.println("\n--- Scenario 4: High Concurrency Write ---");
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for(int i=0; i<100; i++) {
            final int id = i;
            pool.submit(() -> service.addTags(new TagRequest("doc_" + id, Arrays.asList("report"))));
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.SECONDS);

        // Allow indexer to catch up
        Thread.sleep(200);
        System.out.println("Entities tagged 'report': " + service.searchByTag("report").size());
    }
}


