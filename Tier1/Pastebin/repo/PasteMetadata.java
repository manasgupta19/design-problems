package Tier1.Pastebin.repo;

// Simulating Database with TTL support
public class PasteMetadata {
    String key;
    long expirationTimestamp; // Unix Epoch
    String s3Path;

    public PasteMetadata(String key, long expirationTimestamp, String s3Path) {
        this.key = key;
        this.expirationTimestamp = expirationTimestamp;
        this.s3Path = s3Path;
    }

    public long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public String getS3Path() {
        return s3Path;
    }
}
