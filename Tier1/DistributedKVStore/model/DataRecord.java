package Tier1.DistributedKVStore.model;

public class DataRecord {
    String value;
    long timestamp;
    public DataRecord(String v, long t) { this.value = v; this.timestamp = t; }

    public String getValue() { return value; }
    public long getTimestamp() { return timestamp; }
}
