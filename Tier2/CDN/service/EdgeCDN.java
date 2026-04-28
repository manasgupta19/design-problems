package Tier2.CDN.service;

import Tier2.CDN.model.Response;

public interface EdgeCDN {
    /**
     * Retrieves content.
     * If cached, returns immediately.
     * If missing, coalesces requests and fetches from Origin.
     */
    Response get(String resourcePath);

    /**
     * Forces invalidation of a resource across the cluster.
     * Returns true if purge signals were sent.
     */
    boolean purge(String resourcePath);
}