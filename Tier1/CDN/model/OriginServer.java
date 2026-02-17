package Tier1.CDN.model;

// 1. MOCK ORIGIN SERVER (Simulates S3/Latency)
public class OriginServer {
    public byte[] fetch(String path) {
        System.out.println("[Origin] Processing heavy fetch for: " + path);
        try { Thread.sleep(200); } catch (InterruptedException e) {} // Simulate Latency
        return ("Content-of-" + path).getBytes();
    }
}
