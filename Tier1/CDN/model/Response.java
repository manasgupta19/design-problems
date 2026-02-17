package Tier1.CDN.model;

// Helper Response Object
public class Response {
    String path;
    String data;
    boolean cacheHit;

    public Response(String p, byte[] d, boolean h) {
        this.path = p;
        this.data = (d != null) ? new String(d) : null;
        this.cacheHit = h;
    }

    public boolean isCacheHit() {
        return cacheHit;
    }

    public String getData() {
        return data;
    }

    public String getPath() {
        return path;
    }
}
