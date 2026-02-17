package Tier1.DistributedIdGenerator.model;

public class SnowflakeConfig {
    public static final long EPOCH = 1704067200000L; // Jan 1, 2024
    public static final long WORKER_ID_BITS = 10L;
    public static final long SEQUENCE_BITS = 12L;
}
