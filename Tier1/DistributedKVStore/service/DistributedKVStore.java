package Tier1.DistributedKVStore.service;

public interface DistributedKVStore {
    /**
     * @param key The key.
     * @param value The object to store.
     * @param w Write Quorum (how many ACKs to wait for).
     */
    void put(String key, String value, int w);

    /**
     * @param key The key.
     * @param r Read Quorum (how many nodes to query).
     * @return The most recent value (resolved via LWW or Vector Clock).
     */
    String get(String key, int r);
}
