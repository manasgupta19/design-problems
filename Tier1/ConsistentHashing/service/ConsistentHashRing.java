package Tier1.ConsistentHashing.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.TreeMap;

// 1. Core Router Class
public class ConsistentHashRing implements ConsistentHashRouter {

    // The Ring: Maps Hash(VirtualNode) -> PhysicalNode
    private final TreeMap<Long, String> ring = new TreeMap<>();

    // Config: How many virtual points per physical node
    private final int numberOfReplicas;

    public ConsistentHashRing(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    @Override
    public void addNode(String nodeIp) {
        for (int i = 0; i < numberOfReplicas; i++) {
            // Create unique ID for virtual node, e.g., "192.168.1.1:0"
            long hash = computeHash(nodeIp + ":" + i);
            ring.put(hash, nodeIp);
        }
        System.out.println("Added Node " + nodeIp + ". Ring Size: " + ring.size());
    }

    @Override
    public void removeNode(String nodeIp) {
        for (int i = 0; i < numberOfReplicas; i++) {
            long hash = computeHash(nodeIp + ":" + i);
            ring.remove(hash);
        }
        System.out.println("Removed Node " + nodeIp + ". Ring Size: " + ring.size());
    }

    @Override
    public String getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }

        long hash = computeHash(key);

        // 1. Find the portion of the ring >= hash
        SortedMap<Long, String> tailMap = ring.tailMap(hash);

        // 2. Determine target hash
        long targetHash;
        if (!tailMap.isEmpty()) {
            // Found a node clockwise
            targetHash = tailMap.firstKey();
        } else {
            // Wrap around to the start of the ring
            targetHash = ring.firstKey();
        }

        return ring.get(targetHash);
    }

    // Helper: MD5 Hash to map string to 32-bit Integer (Long for unsigned safety)
    private long computeHash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
            byte[] digest = md.digest();

            // Extract first 4 bytes to create a 32-bit integer
            // & 0xFF converts signed byte to unsigned int component
            long h = ((long) (digest[0] & 0xFF) << 24) |
                     ((long) (digest[1] & 0xFF) << 16) |
                     ((long) (digest[2] & 0xFF) << 8)  |
                     ((long) (digest[3] & 0xFF));

            return h & 0xFFFFFFFFL; // Ensure positive 32-bit value
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
    }

    // For visualization/debug
    public void printDistribution() {
        System.out.println("--- Ring Distribution ---");
        for (Long hash : ring.keySet()) {
            System.out.println(hash + " -> " + ring.get(hash));
        }
    }
}
