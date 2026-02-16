package Tier1.ConsistentHashing.service;

public interface ConsistentHashRouter {
    /**
     * Adds a physical server to the ring.
     * Creates 'replicas' number of virtual nodes.
     */
    void addNode(String nodeIp);

    /**
     * Removes a physical server from the ring.
     * Removes all associated virtual nodes.
     */
    void removeNode(String nodeIp);

    /**
     * Routes a key to a specific node.
     * @return The physical node IP responsible for the key.
     */
    String getNode(String key);
}


