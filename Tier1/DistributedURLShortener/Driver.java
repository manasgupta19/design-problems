package Tier1.DistributedURLShortener;

import Tier1.DistributedURLShortener.service.UrlShortenerService;
import Tier1.DistributedURLShortener.service.UrlShortenerServiceImpl;

// ---------------------------------------------------------
// 4. DRIVER (Simulation)
// ---------------------------------------------------------
public class Driver {
    public static void main(String[] args) {
        UrlShortenerService service = new UrlShortenerServiceImpl();

        System.out.println("--- Scenario 1: Basic Shortening ---");
        String longUrl = "https://www.google.com/search?q=system+design";
        String shortUrl = service.createShortLink(longUrl, "User1");
        System.out.println("Long: " + longUrl);
        System.out.println("Short: " + shortUrl);

        System.out.println("\n--- Scenario 2: Redirection ---");
        String original = service.getOriginalUrl(shortUrl);
        System.out.println("Retrieved: " + original);
        System.out.println("Match: " + original.equals(longUrl));

        System.out.println("\n--- Scenario 3: Idempotency ---");
        String retryShortUrl = service.createShortLink(longUrl, "User1");
        System.out.println("Retry Short: " + retryShortUrl);
        System.out.println("Is Same Object? " + (shortUrl.equals(retryShortUrl)));
    }
}

