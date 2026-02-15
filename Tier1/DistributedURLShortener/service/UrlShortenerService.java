package Tier1.DistributedURLShortener.service;

public interface UrlShortenerService {
    /**
     * Creates a short alias.
     * @param longUrl The original URL.
     * @param userId (Optional) for analytics/rate limiting.
     * @return The shortened URL string (Base62).
     */
    String createShortLink(String longUrl, String userId);

    /**
     * Resolves a short alias.
     * @param shortUrl The Base62 key.
     * @return The original long URL.
     * @throws NotFoundException if key doesn't exist.
     */
    String getOriginalUrl(String shortUrl);
}