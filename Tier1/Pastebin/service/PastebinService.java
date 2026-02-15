package Tier1.Pastebin.service;

public interface PastebinService {
    /**
     * Creates a new paste.
     * @param content The text content.
     * @param durationSeconds How long the paste should live.
     * @return The unique short key (e.g., "AbC1").
     * @throws StorageLimitExceededException if content > 10MB.
     */
    String createPaste(String content, int durationSeconds);

    /**
     * Retrieves a paste.
     * @param key The short key.
     * @return The content.
     * @throws PasteNotFoundException if key invalid or expired.
     */
    String getPaste(String key);
}


