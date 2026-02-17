package Tier1.DistributedIdGenerator.service;

public class SystemTimeSource implements TimeSource {
    @Override
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
